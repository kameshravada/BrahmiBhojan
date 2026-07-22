package com.brahmibhojan.modules.delivery.service;

import com.brahmibhojan.modules.orders.model.OrderStatus;

import java.util.Locale;

public interface DeliveryStatusMapper {

    boolean supports(String partner);

    OrderStatus normalize(String providerStatus);

    default String canonicalize(String providerStatus) {
        if (providerStatus == null || providerStatus.isBlank()) {
            return "";
        }
        return providerStatus.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }
}


