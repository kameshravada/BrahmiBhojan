package com.brahmibhojan.modules.payments.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentOrderResponse(
        UUID orderId,
        String provider,
        String providerOrderId,
        BigDecimal amount,
        String currency,
        String status
) {
}

