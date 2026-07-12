package com.brahmibhojan.modules.inventory.repository;

import com.brahmibhojan.modules.inventory.model.InventoryReservation;
import com.brahmibhojan.modules.inventory.model.InventoryReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    List<InventoryReservation> findAllByOrderIdAndStatus(UUID orderId, InventoryReservationStatus status);

    List<InventoryReservation> findAllByOrderId(UUID orderId);
}


