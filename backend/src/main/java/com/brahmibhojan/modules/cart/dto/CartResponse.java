package com.brahmibhojan.modules.cart.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID cartId,
        String guestToken,
        int itemCount,
        BigDecimal totalAmount,
        List<CartItemResponse> items
) {
}

