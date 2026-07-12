package com.brahmibhojan.modules.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.brahmibhojan.modules.auth.dto.OtpRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final String CHALLENGE_KEY_PREFIX = "auth:otp:challenge:";
    private static final String COOLDOWN_KEY_PREFIX = "auth:otp:cooldown:";
    private static final String RATE_REQ_MOBILE_PREFIX = "auth:otp:rate:req:mobile:";
    private static final String RATE_REQ_IP_PREFIX = "auth:otp:rate:req:ip:";
    private static final String RATE_REQ_DEVICE_PREFIX = "auth:otp:rate:req:device:";
    private static final String RATE_VERIFY_DEVICE_PREFIX = "auth:otp:rate:verify:device:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final OtpDeliveryGateway otpDeliveryGateway;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.otp.length:6}")
    private int otpLength;

    @Value("${auth.otp.expiry-seconds:300}")
    private long otpExpirySeconds;

    @Value("${auth.otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${auth.otp.request-cooldown-seconds:60}")
    private long requestCooldownSeconds;

    @Value("${auth.otp.hash-pepper:change-me-in-prod}")
    private String otpHashPepper;

    @Value("${auth.otp.return-otp-in-response:false}")
    private boolean returnOtpInResponse;

    @Value("${auth.otp.rate-limit.mobile.max-requests:5}")
    private long mobileRateLimit;

    @Value("${auth.otp.rate-limit.ip.max-requests:20}")
    private long ipRateLimit;

    @Value("${auth.otp.rate-limit.device.max-requests:10}")
    private long deviceRateLimit;

    @Value("${auth.otp.rate-limit.window-seconds:3600}")
    private long rateLimitWindowSeconds;

    @Value("${auth.otp.rate-limit.verify-device.max-attempts:25}")
    private long verifyDeviceLimit;

    public OtpRequestResponse requestOtp(String mobile, String deviceId, String ipAddress) {
        String normalizedMobile = normalizeMobile(mobile);
        String normalizedDevice = normalizeDevice(deviceId);
        String normalizedIp = normalizeIp(ipAddress);

        enforceRequestRateLimits(normalizedMobile, normalizedDevice, normalizedIp);

        String cooldownKey = COOLDOWN_KEY_PREFIX + normalizedMobile;

        if (redisTemplate.hasKey(cooldownKey)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Please wait before requesting another OTP");
        }

        String challengeId = UUID.randomUUID().toString();
        String otp = generateOtp(otpLength);
        OtpChallengeCache challenge = new OtpChallengeCache(
                challengeId,
                normalizedMobile,
                hashOtp(otp, challengeId),
                Instant.now().plusSeconds(otpExpirySeconds).getEpochSecond(),
                0
        );

        storeChallenge(challengeId, challenge, otpExpirySeconds);
        redisTemplate.opsForValue().set(cooldownKey, "1", requestCooldownSeconds, TimeUnit.SECONDS);
        otpDeliveryGateway.sendOtp(normalizedMobile, otp);

        return new OtpRequestResponse(challengeId, otpExpirySeconds, returnOtpInResponse ? otp : null);
    }

    public void validateOtp(String challengeId, String mobile, String otp, String deviceId) {
        OtpChallengeCache challenge = readChallenge(challengeId);
        String normalizedMobile = normalizeMobile(mobile);
        String normalizedDevice = normalizeDevice(deviceId);

        enforceVerifyRateLimit(normalizedMobile, normalizedDevice);

        if (!challenge.mobile().equals(normalizedMobile)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP challenge");
        }

        if (challenge.expiresAtEpochSeconds() <= Instant.now().getEpochSecond()) {
            redisTemplate.delete(CHALLENGE_KEY_PREFIX + challengeId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OTP has expired");
        }

        if (challenge.attempts() >= maxAttempts) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Maximum OTP attempts exceeded");
        }

        String expectedHash = challenge.otpHash();
        String providedHash = hashOtp(otp, challengeId);
        if (!MessageDigest.isEqual(expectedHash.getBytes(StandardCharsets.UTF_8), providedHash.getBytes(StandardCharsets.UTF_8))) {
            int attempts = challenge.attempts() + 1;
            storeChallenge(challengeId, challenge.withAttempts(attempts), ttlSeconds(challenge.expiresAtEpochSeconds()));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }

        redisTemplate.delete(CHALLENGE_KEY_PREFIX + challengeId);
    }

    public String normalizeMobile(String mobile) {
        String normalized = mobile == null ? "" : mobile.replaceAll("[\\s-]", "").trim();
        if (!normalized.matches("^\\+?[1-9]\\d{9,14}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mobile number format");
        }
        return normalized;
    }

    private String normalizeDevice(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return "unknown-device";
        }
        return deviceId.trim().toLowerCase();
    }

    private String normalizeIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return "unknown-ip";
        }
        return ipAddress.trim();
    }

    private void enforceRequestRateLimits(String mobile, String deviceId, String ipAddress) {
        long mobileCount = incrementRateCounter(RATE_REQ_MOBILE_PREFIX + mobile, rateLimitWindowSeconds);
        if (mobileCount > mobileRateLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "OTP request limit reached for mobile");
        }

        long ipCount = incrementRateCounter(RATE_REQ_IP_PREFIX + ipAddress, rateLimitWindowSeconds);
        if (ipCount > ipRateLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "OTP request limit reached for IP");
        }

        long deviceCount = incrementRateCounter(RATE_REQ_DEVICE_PREFIX + deviceId, rateLimitWindowSeconds);
        if (deviceCount > deviceRateLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "OTP request limit reached for device");
        }
    }

    private void enforceVerifyRateLimit(String mobile, String deviceId) {
        String key = RATE_VERIFY_DEVICE_PREFIX + mobile + ":" + deviceId;
        long verifyCount = incrementRateCounter(key, rateLimitWindowSeconds);
        if (verifyCount > verifyDeviceLimit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "OTP verification attempts exceeded for device");
        }
    }

    private long incrementRateCounter(String key, long windowSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rate limiter unavailable");
        }
        if (count == 1L) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }
        return count;
    }

    private OtpChallengeCache readChallenge(String challengeId) {
        String raw = redisTemplate.opsForValue().get(CHALLENGE_KEY_PREFIX + challengeId);
        if (raw == null || raw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OTP challenge not found or expired");
        }

        try {
            return objectMapper.readValue(raw, OtpChallengeCache.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP challenge parsing failed");
        }
    }

    private void storeChallenge(String challengeId, OtpChallengeCache challenge, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(challenge);
            redisTemplate.opsForValue().set(CHALLENGE_KEY_PREFIX + challengeId, json, ttlSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP challenge storage failed");
        }
    }

    private long ttlSeconds(long expiresAtEpochSeconds) {
        long ttl = expiresAtEpochSeconds - Instant.now().getEpochSecond();
        return Math.max(ttl, 1);
    }

    private String generateOtp(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int value = secureRandom.nextInt((max - min) + 1) + min;
        return String.valueOf(value);
    }

    private String hashOtp(String otp, String challengeId) {
        String value = otp + ":" + challengeId + ":" + otpHashPepper;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "OTP hashing failed");
        }
    }

    private record OtpChallengeCache(
            String challengeId,
            String mobile,
            String otpHash,
            long expiresAtEpochSeconds,
            int attempts
    ) {
        private OtpChallengeCache withAttempts(int newAttempts) {
            return new OtpChallengeCache(challengeId, mobile, otpHash, expiresAtEpochSeconds, newAttempts);
        }
    }
}


