package com.brahmibhojan.modules.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantResponse(
        UUID variantId,
        String label,
        BigDecimal quantityValue,
        String quantityUnit,
        BigDecimal price,
        BigDecimal mrp,
        boolean defaultVariant
) {
}

