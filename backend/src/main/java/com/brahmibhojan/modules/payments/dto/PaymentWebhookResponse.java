package com.brahmibhojan.modules.payments.dto;

public record PaymentWebhookResponse(
        String eventId,
        String providerOrderId,
        String result
) {
}

