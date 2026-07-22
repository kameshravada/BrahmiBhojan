package com.brahmibhojan.modules.delivery.service;

import com.brahmibhojan.modules.orders.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DelhiveryStatusMapper implements DeliveryStatusMapper {

    private static final String DELHIVERY = "DELHIVERY";

    @Override
    public boolean supports(String partner) {
        return DELHIVERY.equals(partner);
    }

    @Override
    public OrderStatus normalize(String providerStatus) {
        String normalized = canonicalize(providerStatus);
        return switch (normalized) {
            case "packed", "manifested", "ready_to_ship", "pickup_done", "pickup_scheduled" -> OrderStatus.PACKED;
            case "shipped", "in_transit", "dispatched", "shipment_dispatched", "reached_destination_hub" -> OrderStatus.SHIPPED;
            case "out_for_delivery", "ofd" -> OrderStatus.OUT_FOR_DELIVERY;
            case "delivered" -> OrderStatus.DELIVERED;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported Delhivery status");
        };
    }
}


