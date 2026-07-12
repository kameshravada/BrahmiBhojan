package com.brahmibhojan.modules.auth.dto;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String mobile,
        String email,
        String fullName,
        String role,
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {
}

