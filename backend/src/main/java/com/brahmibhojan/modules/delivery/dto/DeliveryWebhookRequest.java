package com.brahmibhojan.modules.delivery.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliveryWebhookRequest(
        @NotBlank(message = "Partner is required")
        String partner,

        @NotBlank(message = "Partner event id is required")
        String eventId,

        @NotBlank(message = "Order number is required")
        String orderNumber,

        String trackingId,

        @NotBlank(message = "Status is required")
        String status,

        String remark
) {
}

