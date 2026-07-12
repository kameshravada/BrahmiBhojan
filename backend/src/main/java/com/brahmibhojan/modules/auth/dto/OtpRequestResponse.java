package com.brahmibhojan.modules.auth.dto;

public record OtpRequestResponse(
        String challengeId,
        long expiresInSeconds,
        String otpPreview
) {
}

