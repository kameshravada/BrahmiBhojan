package com.brahmibhojan.modules.checkout.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckoutValidateResponse(
        UUID cartId,
        UUID addressId,
        int itemCount,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal deliveryFee,
        BigDecimal payableAmount,
        List<CheckoutItemResponse> items
) {
}

