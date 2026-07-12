package com.brahmibhojan.modules.customer.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerProfileResponse(
        UUID userId,
        String mobile,
        String fullName,
        String email,
        Instant mobileVerifiedAt,
        String status
) {
}

