package com.brahmibhojan.modules.delivery.dto;

public record DeliveryWebhookResponse(
        String eventId,
        String orderNumber,
        String result,
        String normalizedStatus
) {
}

