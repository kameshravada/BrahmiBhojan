package com.brahmibhojan.modules.checkout.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutValidateRequest(
        @NotNull(message = "Address id is required")
        UUID addressId
) {
}

