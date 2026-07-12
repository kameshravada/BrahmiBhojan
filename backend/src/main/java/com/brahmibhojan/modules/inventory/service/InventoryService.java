package com.brahmibhojan.modules.inventory.service;

import com.brahmibhojan.modules.inventory.dto.InventoryReserveItem;
import com.brahmibhojan.modules.inventory.model.InventoryReservation;
import com.brahmibhojan.modules.inventory.model.InventoryReservationStatus;
import com.brahmibhojan.modules.inventory.model.InventoryStock;
import com.brahmibhojan.modules.inventory.repository.InventoryReservationRepository;
import com.brahmibhojan.modules.inventory.repository.InventoryStockRepository;
import com.brahmibhojan.modules.orders.model.OrderItem;
import com.brahmibhojan.modules.orders.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryStockRepository inventoryStockRepository;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${inventory.default-available-quantity:100}")
    private int defaultAvailableQuantity;

    @Transactional
    public void reserveForOrder(UUID orderId, List<InventoryReserveItem> items) {
        for (InventoryReserveItem item : items) {
            if (item.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid inventory reservation quantity");
            }

            InventoryStock stock = inventoryStockRepository.findByVariantIdForUpdate(item.variantId())
                    .orElseGet(() -> createDefaultStock(item.variantId()));

            int sellableQuantity = stock.getAvailableQuantity() - stock.getReservedQuantity();
            if (sellableQuantity < item.quantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient inventory for variant " + item.variantId());
            }

            stock.setReservedQuantity(stock.getReservedQuantity() + item.quantity());
            inventoryStockRepository.save(stock);

            InventoryReservation reservation = new InventoryReservation();
            reservation.setOrderId(orderId);
            reservation.setVariantId(item.variantId());
            reservation.setQuantity(item.quantity());
            reservation.setStatus(InventoryReservationStatus.ACTIVE);
            inventoryReservationRepository.save(reservation);
        }
    }

    @Transactional
    public void releaseReservationsForOrder(UUID orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderId(orderId);
        List<InventoryReservation> activeReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == InventoryReservationStatus.ACTIVE)
                .toList();

        if (activeReservations.isEmpty()) {
            rollbackReservedQuantityFromOrderItems(orderId);
            return;
        }

        for (InventoryReservation reservation : activeReservations) {
            InventoryStock stock = inventoryStockRepository.findByVariantIdForUpdate(reservation.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Inventory stock missing"));

            stock.setReservedQuantity(Math.max(stock.getReservedQuantity() - reservation.getQuantity(), 0));
            inventoryStockRepository.save(stock);

            reservation.setStatus(InventoryReservationStatus.RELEASED);
            inventoryReservationRepository.save(reservation);
        }
    }

    @Transactional
    public void consumeReservationsForOrder(UUID orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderId(orderId);
        List<InventoryReservation> activeReservations = reservations.stream()
                .filter(reservation -> reservation.getStatus() == InventoryReservationStatus.ACTIVE)
                .toList();

        if (activeReservations.isEmpty()) {
            consumeFromOrderItems(orderId);
            return;
        }

        for (InventoryReservation reservation : activeReservations) {
            InventoryStock stock = inventoryStockRepository.findByVariantIdForUpdate(reservation.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Inventory stock missing"));

            stock.setReservedQuantity(Math.max(stock.getReservedQuantity() - reservation.getQuantity(), 0));
            stock.setAvailableQuantity(Math.max(stock.getAvailableQuantity() - reservation.getQuantity(), 0));
            inventoryStockRepository.save(stock);

            reservation.setStatus(InventoryReservationStatus.CONSUMED);
            inventoryReservationRepository.save(reservation);
        }
    }

    private InventoryStock createDefaultStock(UUID variantId) {
        InventoryStock stock = new InventoryStock();
        stock.setVariantId(variantId);
        stock.setAvailableQuantity(defaultAvailableQuantity);
        stock.setReservedQuantity(0);
        return inventoryStockRepository.save(stock);
    }

    private void rollbackReservedQuantityFromOrderItems(UUID orderId) {
        for (Map.Entry<UUID, Integer> entry : getOrderItemVariantQuantities(orderId).entrySet()) {
            InventoryStock stock = inventoryStockRepository.findByVariantIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Inventory stock missing"));
            int releasable = Math.min(stock.getReservedQuantity(), entry.getValue());
            stock.setReservedQuantity(Math.max(stock.getReservedQuantity() - releasable, 0));
            inventoryStockRepository.save(stock);
        }
    }

    private void consumeFromOrderItems(UUID orderId) {
        for (Map.Entry<UUID, Integer> entry : getOrderItemVariantQuantities(orderId).entrySet()) {
            InventoryStock stock = inventoryStockRepository.findByVariantIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Inventory stock missing"));
            int consumable = Math.min(stock.getReservedQuantity(), entry.getValue());
            stock.setReservedQuantity(Math.max(stock.getReservedQuantity() - consumable, 0));
            stock.setAvailableQuantity(Math.max(stock.getAvailableQuantity() - consumable, 0));
            inventoryStockRepository.save(stock);
        }
    }

    private Map<UUID, Integer> getOrderItemVariantQuantities(UUID orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getVariantId, Collectors.summingInt(OrderItem::getQuantity)));
    }
}




