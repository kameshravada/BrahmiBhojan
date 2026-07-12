package com.brahmibhojan.modules.payments.controller;

import com.brahmibhojan.modules.payments.dto.CreatePaymentOrderRequest;
import com.brahmibhojan.modules.payments.dto.CreatePaymentOrderResponse;
import com.brahmibhojan.modules.payments.dto.PaymentWebhookRequest;
import com.brahmibhojan.modules.payments.dto.PaymentWebhookResponse;
import com.brahmibhojan.modules.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/v1/payments/orders")
    public CreatePaymentOrderResponse createPaymentOrder(
            Authentication authentication,
            @Valid @RequestBody CreatePaymentOrderRequest request
    ) {
        return paymentService.createPaymentOrder(authentication.getName(), request);
    }

    @PostMapping("/api/v1/payments/webhook")
    public PaymentWebhookResponse reconcileWebhook(
            @RequestHeader(value = "X-Payment-Signature", required = false) String signature,
            @Valid @RequestBody PaymentWebhookRequest request
    ) {
        return paymentService.reconcileWebhook(request, signature);
    }
}

