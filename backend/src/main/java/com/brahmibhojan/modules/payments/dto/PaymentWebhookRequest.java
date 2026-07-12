package com.brahmibhojan.modules.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentWebhookRequest(
        @NotBlank(message = "Provider event id is required")
        String eventId,

        @NotBlank(message = "Provider order id is required")
        String providerOrderId,

        String providerPaymentId,

        @NotBlank(message = "Status is required")
        String status,

        @NotNull(message = "Amount is required")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        String currency
) {
}

