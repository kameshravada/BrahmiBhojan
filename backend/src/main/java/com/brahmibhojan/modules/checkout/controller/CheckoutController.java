package com.brahmibhojan.modules.checkout.controller;

import com.brahmibhojan.modules.checkout.dto.CheckoutValidateRequest;
import com.brahmibhojan.modules.checkout.dto.CheckoutValidateResponse;
import com.brahmibhojan.modules.checkout.dto.CreateOrderRequest;
import com.brahmibhojan.modules.checkout.dto.CreateOrderResponse;
import com.brahmibhojan.modules.checkout.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/api/v1/checkout/validate")
    public CheckoutValidateResponse validateCheckout(
            Authentication authentication,
            @Valid @RequestBody CheckoutValidateRequest request
    ) {
        return checkoutService.validateCheckout(authentication.getName(), request.addressId());
    }

    @PostMapping("/api/v1/checkout/orders")
    public CreateOrderResponse createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return checkoutService.createOrder(authentication.getName(), request);
    }
}

