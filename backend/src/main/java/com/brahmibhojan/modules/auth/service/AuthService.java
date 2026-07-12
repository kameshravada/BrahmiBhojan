package com.brahmibhojan.modules.auth.service;

import com.brahmibhojan.modules.auth.dto.AuthResponse;
import com.brahmibhojan.modules.auth.dto.LogoutRequest;
import com.brahmibhojan.modules.auth.dto.OtpRequestDto;
import com.brahmibhojan.modules.auth.dto.OtpRequestResponse;
import com.brahmibhojan.modules.auth.dto.RefreshTokenRequest;
import com.brahmibhojan.modules.auth.dto.OtpVerifyDto;
import com.brahmibhojan.modules.users.model.Role;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.model.UserStatus;
import com.brahmibhojan.modules.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.brahmibhojan.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public OtpRequestResponse requestOtp(OtpRequestDto request, HttpServletRequest httpRequest) {
        return otpService.requestOtp(request.mobile(), request.deviceId(), extractClientIp(httpRequest));
    }

    public AuthResponse verifyOtpAndLogin(OtpVerifyDto request) {
        otpService.validateOtp(request.challengeId(), request.mobile(), request.otp(), request.deviceId());
        String normalizedMobile = otpService.normalizeMobile(request.mobile());
        User user = userRepository.findByMobile(normalizedMobile).orElseGet(() -> createUser(normalizedMobile));
        if (user.getMobileVerifiedAt() == null) {
            user.setMobileVerifiedAt(Instant.now());
            user.setStatus(UserStatus.ACTIVE);
            user = userRepository.save(user);
        }
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshSession(RefreshTokenRequest request) {
        User user = refreshTokenService.getValidUser(request.refreshToken());
        String rotatedRefreshToken = refreshTokenService.rotate(request.refreshToken());
        return buildAuthResponse(user, rotatedRefreshToken);
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private User createUser(String mobile) {
        User user = new User();
        user.setMobile(mobile);
        user.setFullName("BB user");
        user.setRole(Role.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setMobileVerifiedAt(Instant.now());
        return userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String refreshToken = refreshTokenService.issue(user);
        return buildAuthResponse(user, refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String refreshToken) {
        String token = jwtService.generateToken(user.getMobile(), user.getRole().name());
        return new AuthResponse(
                user.getId(),
                user.getMobile(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                token,
                refreshToken,
                jwtService.getExpirationSeconds()
        );
    }
}

