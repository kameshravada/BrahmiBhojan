package com.brahmibhojan.modules.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID productId,
        String name,
        String slug,
        BigDecimal startingPrice,
        String defaultVariantLabel,
        String categorySlug
) {
}

