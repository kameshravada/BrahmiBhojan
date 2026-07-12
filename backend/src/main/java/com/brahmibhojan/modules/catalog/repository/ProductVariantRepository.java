package com.brahmibhojan.modules.catalog.repository;

import com.brahmibhojan.modules.catalog.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findAllByProductIdAndAvailableTrueOrderBySortOrderAsc(UUID productId);

    Optional<ProductVariant> findByProductIdAndDefaultVariantTrueAndAvailableTrue(UUID productId);

    Optional<ProductVariant> findByIdAndAvailableTrue(UUID variantId);
}

