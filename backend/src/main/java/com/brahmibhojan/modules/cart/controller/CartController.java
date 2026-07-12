package com.brahmibhojan.modules.cart.controller;

import com.brahmibhojan.modules.cart.dto.AddCartItemRequest;
import com.brahmibhojan.modules.cart.dto.CartResponse;
import com.brahmibhojan.modules.cart.dto.MergeCartRequest;
import com.brahmibhojan.modules.cart.dto.UpdateCartItemRequest;
import com.brahmibhojan.modules.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/api/v1/cart/items")
    public CartResponse addItem(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(authenticatedMobile(authentication), guestToken, request);
    }

    @PostMapping("/api/v1/cart/merge")
    public CartResponse mergeGuestCart(Authentication authentication, @Valid @RequestBody MergeCartRequest request) {
        return cartService.mergeGuestCart(authenticatedMobile(authentication), request.guestToken());
    }

    @PatchMapping("/api/v1/cart/items/{itemId}")
    public CartResponse updateItem(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(authenticatedMobile(authentication), guestToken, itemId, request);
    }

    @GetMapping("/api/v1/cart")
    public CartResponse getCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken
    ) {
        return cartService.getCart(authenticatedMobile(authentication), guestToken);
    }

    @DeleteMapping("/api/v1/cart/items/{itemId}")
    public CartResponse removeItem(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @PathVariable UUID itemId
    ) {
        return cartService.removeItem(authenticatedMobile(authentication), guestToken, itemId);
    }

    private String authenticatedMobile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return authentication.getName();
    }
}

