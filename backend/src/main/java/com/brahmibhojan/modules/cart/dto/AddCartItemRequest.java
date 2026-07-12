package com.brahmibhojan.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemRequest(
        @NotNull(message = "Variant id is required")
        UUID variantId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {
}

