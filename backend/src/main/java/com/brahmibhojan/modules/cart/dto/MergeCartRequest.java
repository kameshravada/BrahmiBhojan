package com.brahmibhojan.modules.cart.dto;

import jakarta.validation.constraints.NotBlank;

public record MergeCartRequest(
        @NotBlank(message = "Guest token is required")
        String guestToken
) {
}

