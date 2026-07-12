package com.brahmibhojan.modules.checkout.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutItemResponse(
        UUID productId,
        String productName,
        UUID variantId,
        String variantLabel,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}

