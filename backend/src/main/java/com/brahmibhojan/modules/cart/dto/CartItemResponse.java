package com.brahmibhojan.modules.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID itemId,
        UUID productId,
        String productName,
        String productSlug,
        UUID variantId,
        String variantLabel,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}

