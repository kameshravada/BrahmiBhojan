package com.brahmibhojan.modules.inventory.repository;

import com.brahmibhojan.modules.inventory.model.InventoryStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {

    Optional<InventoryStock> findByVariantId(UUID variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select stock from InventoryStock stock where stock.variantId = :variantId")
    Optional<InventoryStock> findByVariantIdForUpdate(@Param("variantId") UUID variantId);
}

