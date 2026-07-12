package com.brahmibhojan.modules.payments.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePaymentOrderRequest(
        @NotNull(message = "Order id is required")
        UUID orderId
) {
}

