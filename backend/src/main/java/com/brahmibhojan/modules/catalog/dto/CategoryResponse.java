package com.brahmibhojan.modules.catalog.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID categoryId,
        String name,
        String slug,
        String description
) {
}

