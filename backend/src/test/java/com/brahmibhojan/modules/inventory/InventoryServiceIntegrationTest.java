package com.brahmibhojan.modules.inventory;

import com.brahmibhojan.modules.inventory.dto.InventoryReserveItem;
import com.brahmibhojan.modules.inventory.repository.InventoryStockRepository;
import com.brahmibhojan.modules.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class InventoryServiceIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    @Test
    void reserveForOrderShouldRejectWhenQuantityExceedsSellable() {
        UUID variantId = UUID.randomUUID();

        assertThatThrownBy(() -> inventoryService.reserveForOrder(
                UUID.randomUUID(),
                List.of(new InventoryReserveItem(variantId, 101))
        ))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        assertThat(inventoryStockRepository.findByVariantId(variantId)).isEmpty();
    }

    @Test
    void concurrentReservationsShouldNotOverReserveStock() throws Exception {
        UUID variantId = UUID.randomUUID();
        UUID seedOrderId = UUID.randomUUID();

        // Seed stock row upfront to avoid concurrent first-insert races.
        inventoryService.reserveForOrder(seedOrderId, List.of(new InventoryReserveItem(variantId, 1)));
        inventoryService.releaseReservationsForOrder(seedOrderId);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startGate = new CountDownLatch(1);

        Callable<Boolean> reserveTask = () -> {
            startGate.await();
            try {
                inventoryService.reserveForOrder(
                        UUID.randomUUID(),
                        List.of(new InventoryReserveItem(variantId, 60))
                );
                return true;
            } catch (ResponseStatusException ex) {
                return false;
            }
        };

        Future<Boolean> first = executor.submit(reserveTask);
        Future<Boolean> second = executor.submit(reserveTask);
        startGate.countDown();

        boolean firstSuccess = first.get();
        boolean secondSuccess = second.get();
        executor.shutdown();

        int successCount = (firstSuccess ? 1 : 0) + (secondSuccess ? 1 : 0);
        assertThat(successCount).isEqualTo(1);

        var stock = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        assertThat(stock.getAvailableQuantity()).isEqualTo(100);
        assertThat(stock.getReservedQuantity()).isEqualTo(60);
    }
}

