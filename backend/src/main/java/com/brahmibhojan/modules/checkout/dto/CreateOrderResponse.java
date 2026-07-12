package com.brahmibhojan.modules.checkout.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String orderNumber,
        String orderStatus,
        String paymentStatus,
        BigDecimal payableAmount
) {
}

