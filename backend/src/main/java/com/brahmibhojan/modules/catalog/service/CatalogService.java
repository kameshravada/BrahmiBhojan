package com.brahmibhojan.modules.catalog.service;

import com.brahmibhojan.modules.catalog.dto.CategoryResponse;
import com.brahmibhojan.modules.catalog.dto.ProductDetailResponse;
import com.brahmibhojan.modules.catalog.dto.ProductSummaryResponse;
import com.brahmibhojan.modules.catalog.dto.ProductVariantResponse;
import com.brahmibhojan.modules.catalog.model.Category;
import com.brahmibhojan.modules.catalog.model.Product;
import com.brahmibhojan.modules.catalog.model.ProductVariant;
import com.brahmibhojan.modules.catalog.repository.CategoryRepository;
import com.brahmibhojan.modules.catalog.repository.ProductRepository;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAllByActiveTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProducts(
            String categorySlug,
            String query,
            String unit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 100));
        String resolvedSort = resolveSort(sort);
        Page<Product> products = productRepository.searchProducts(
                normalizeFilter(categorySlug),
                normalizeFilter(query),
                normalizeFilter(unit),
                minPrice,
                maxPrice,
                resolvedSort,
                pageable
        );

        return products.map(this::toProductSummaryResponse);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndAvailableTrue(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toProductDetailResponse(product);
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }

    private ProductSummaryResponse toProductSummaryResponse(Product product) {
        ProductVariant defaultVariant = getDefaultVariant(product);
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                defaultVariant.getPrice(),
                defaultVariant.getLabel(),
                product.getCategory().getSlug()
        );
    }

    private ProductDetailResponse toProductDetailResponse(Product product) {
        List<ProductVariantResponse> variants = productVariantRepository
                .findAllByProductIdAndAvailableTrueOrderBySortOrderAsc(product.getId())
                .stream()
                .map(this::toVariantResponse)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                variants
        );
    }

    private ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getLabel(),
                variant.getQuantityValue(),
                variant.getQuantityUnit(),
                variant.getPrice(),
                variant.getMrp(),
                variant.isDefaultVariant()
        );
    }

    private ProductVariant getDefaultVariant(Product product) {
        return productVariantRepository.findByProductIdAndDefaultVariantTrueAndAvailableTrue(product.getId())
                .orElseGet(() -> productVariantRepository.findAllByProductIdAndAvailableTrueOrderBySortOrderAsc(product.getId())
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "No active variant found for product " + product.getSlug()
                        )));
    }

    private String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String resolveSort(String sort) {
        return CatalogProductSort.fromRequest(sort).value;
    }

    private enum CatalogProductSort {
        RELEVANCE("relevance"),
        PRICE_ASC("price_asc"),
        PRICE_DESC("price_desc"),
        NEWEST("newest");

        private final String value;

        CatalogProductSort(String value) {
            this.value = value;
        }

        private static CatalogProductSort fromRequest(String raw) {
            if (raw == null || raw.isBlank()) {
                return RELEVANCE;
            }

            String normalized = raw.trim().toLowerCase(Locale.ROOT);
            for (CatalogProductSort option : values()) {
                if (option.value.equals(normalized)) {
                    return option;
                }
            }
            return RELEVANCE;
        }
    }
}

