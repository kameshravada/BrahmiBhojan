package com.brahmibhojan.modules.delivery.controller;

import com.brahmibhojan.modules.delivery.dto.DeliveryWebhookRequest;
import com.brahmibhojan.modules.delivery.dto.DeliveryWebhookResponse;
import com.brahmibhojan.modules.delivery.service.DeliveryTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryTrackingService deliveryTrackingService;

    @PostMapping("/api/v1/delivery/webhook")
    public DeliveryWebhookResponse reconcileWebhook(
            @RequestHeader(value = "X-Delivery-Signature", required = false) String signature,
            @Valid @RequestBody DeliveryWebhookRequest request
    ) {
        return deliveryTrackingService.reconcileWebhook(request, signature);
    }
}

