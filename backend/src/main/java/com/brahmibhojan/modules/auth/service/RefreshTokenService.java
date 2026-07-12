package com.brahmibhojan.modules.auth.service;

import com.brahmibhojan.modules.auth.model.RefreshToken;
import com.brahmibhojan.modules.auth.repository.RefreshTokenRepository;
import com.brahmibhojan.modules.users.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.refresh-token.expiry-seconds:2592000}")
    private long refreshTokenExpirySeconds;

    @Value("${auth.refresh-token.hash-pepper:change-me-refresh-pepper}")
    private String refreshTokenHashPepper;

    @Transactional
    public String issue(User user) {
        String rawToken = generateToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenExpirySeconds));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public String rotate(String rawToken) {
        RefreshToken existing = findActiveToken(rawToken);
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
        return issue(existing.getUser());
    }

    @Transactional
    public void revoke(String rawToken) {
        RefreshToken existing = refreshTokenRepository.findByTokenHashWithUser(hash(rawToken)).orElse(null);
        if (existing != null && !existing.isRevoked()) {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
        }
    }

    public User getValidUser(String rawToken) {
        return findActiveToken(rawToken).getUser();
    }

    private RefreshToken findActiveToken(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHashWithUser(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked");
        }
        return token;
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((value + ":" + refreshTokenHashPepper).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token hashing failed");
        }
    }
}

