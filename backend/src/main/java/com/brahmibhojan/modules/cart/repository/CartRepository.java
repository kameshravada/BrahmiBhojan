package com.brahmibhojan.modules.cart.repository;

import com.brahmibhojan.modules.cart.model.Cart;
import com.brahmibhojan.modules.cart.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);

    Optional<Cart> findByGuestTokenAndStatus(String guestToken, CartStatus status);
}

