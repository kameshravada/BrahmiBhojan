package com.brahmibhojan.modules.inventory.dto;

import java.util.UUID;

public record InventoryReserveItem(
        UUID variantId,
        int quantity
) {
}

