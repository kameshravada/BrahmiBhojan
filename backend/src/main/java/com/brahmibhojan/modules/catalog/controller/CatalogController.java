package com.brahmibhojan.modules.catalog.controller;

import com.brahmibhojan.modules.catalog.dto.CategoryResponse;
import com.brahmibhojan.modules.catalog.dto.ProductDetailResponse;
import com.brahmibhojan.modules.catalog.dto.ProductSummaryResponse;
import com.brahmibhojan.modules.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/api/v1/catalog/categories")
    public List<CategoryResponse> getCategories() {
        return catalogService.getActiveCategories();
    }

    @GetMapping("/api/v1/catalog/products")
    public Page<ProductSummaryResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return catalogService.getProducts(category, query, unit, minPrice, maxPrice, page, size);
    }

    @GetMapping("/api/v1/catalog/products/{slug}")
    public ProductDetailResponse getProductBySlug(@PathVariable String slug) {
        return catalogService.getProductBySlug(slug);
    }
}

