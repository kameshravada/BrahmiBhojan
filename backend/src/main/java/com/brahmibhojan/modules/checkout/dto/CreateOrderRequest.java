package com.brahmibhojan.modules.checkout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "Address id is required")
        UUID addressId,

        @NotBlank(message = "Payment method is required")
        @Size(max = 40, message = "Payment method must not exceed 40 characters")
        String paymentMethod,

        @Size(max = 120, message = "Idempotency key must not exceed 120 characters")
        String idempotencyKey,

        @Size(max = 500, message = "Notes must not exceed 500 characters")
        String notes
) {
}

