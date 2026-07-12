package com.brahmibhojan.modules.cart.service;

import com.brahmibhojan.modules.cart.dto.AddCartItemRequest;
import com.brahmibhojan.modules.cart.dto.CartItemResponse;
import com.brahmibhojan.modules.cart.dto.CartResponse;
import com.brahmibhojan.modules.cart.dto.UpdateCartItemRequest;
import com.brahmibhojan.modules.cart.model.Cart;
import com.brahmibhojan.modules.cart.model.CartItem;
import com.brahmibhojan.modules.cart.model.CartStatus;
import com.brahmibhojan.modules.cart.repository.CartItemRepository;
import com.brahmibhojan.modules.cart.repository.CartRepository;
import com.brahmibhojan.modules.catalog.model.ProductVariant;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public CartResponse addItem(String mobile, String guestToken, AddCartItemRequest request) {
        Cart cart = resolveOrCreateCart(mobile, guestToken);
        ProductVariant variant = productVariantRepository.findByIdAndAvailableTrue(request.variantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product variant not found"));

        CartItem item = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), variant.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(variant.getProduct());
                    newItem.setProductVariant(variant);
                    return newItem;
                });

        int updatedQty = item.getId() == null ? request.quantity() : item.getQuantity() + request.quantity();
        item.setQuantity(updatedQty);
        item.setUnitPrice(variant.getPrice());
        item.setLineTotal(variant.getPrice().multiply(BigDecimal.valueOf(updatedQty)));
        cartItemRepository.save(item);

        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse mergeGuestCart(String mobile, String guestToken) {
        if (mobile == null || mobile.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required to merge cart");
        }
        if (guestToken == null || guestToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guest token is required");
        }

        Cart userCart = resolveOrCreateCart(mobile, null);
        Cart guestCart = cartRepository.findByGuestTokenAndStatus(guestToken, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest cart not found"));

        List<CartItem> guestItems = cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(guestCart.getId());
        for (CartItem guestItem : guestItems) {
            CartItem targetItem = cartItemRepository
                    .findByCartIdAndProductVariantId(userCart.getId(), guestItem.getProductVariant().getId())
                    .orElseGet(() -> {
                        CartItem newItem = new CartItem();
                        newItem.setCart(userCart);
                        newItem.setProduct(guestItem.getProduct());
                        newItem.setProductVariant(guestItem.getProductVariant());
                        return newItem;
                    });

            int qty = (targetItem.getId() == null ? 0 : targetItem.getQuantity()) + guestItem.getQuantity();
            targetItem.setQuantity(qty);
            targetItem.setUnitPrice(guestItem.getUnitPrice());
            targetItem.setLineTotal(guestItem.getUnitPrice().multiply(BigDecimal.valueOf(qty)));
            cartItemRepository.save(targetItem);
            cartItemRepository.delete(guestItem);
        }

        guestCart.setStatus(CartStatus.ABANDONED);
        cartRepository.save(guestCart);

        return toCartResponse(userCart);
    }

    @Transactional
    public CartResponse updateItem(String mobile, String guestToken, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = resolveOrCreateCart(mobile, guestToken);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        item.setQuantity(request.quantity());
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity())));
        cartItemRepository.save(item);

        return toCartResponse(cart);
    }

    public CartResponse getCart(String mobile, String guestToken) {
        Cart cart = resolveOrCreateCart(mobile, guestToken);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String mobile, String guestToken, UUID itemId) {
        Cart cart = resolveOrCreateCart(mobile, guestToken);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        cartItemRepository.delete(item);
        return toCartResponse(cart);
    }

    private Cart resolveOrCreateCart(String mobile, String guestToken) {
        if (mobile != null && !mobile.isBlank()) {
            User user = userRepository.findByMobile(mobile)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            return cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                    .orElseGet(() -> createUserCart(user));
        }

        String normalizedGuestToken = normalizeOrGenerateGuestToken(guestToken);
        return cartRepository.findByGuestTokenAndStatus(normalizedGuestToken, CartStatus.ACTIVE)
                .orElseGet(() -> createGuestCart(normalizedGuestToken));
    }

    private String normalizeOrGenerateGuestToken(String token) {
        if (token == null || token.isBlank()) {
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        }
        return token.trim();
    }

    private Cart createUserCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    private Cart createGuestCart(String guestToken) {
        Cart cart = new Cart();
        cart.setGuestToken(guestToken);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(cart.getId())
                .stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getSlug(),
                        item.getProductVariant().getId(),
                        item.getProductVariant().getLabel(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getLineTotal()
                ))
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getGuestToken(),
                items.size(),
                total,
                items
        );
    }
}

