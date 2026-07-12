package com.brahmibhojan.modules.catalog.dto;

import java.util.List;
import java.util.UUID;

public record ProductDetailResponse(
        UUID productId,
        String name,
        String slug,
        String description,
        String categoryName,
        String categorySlug,
        List<ProductVariantResponse> variants
) {
}

