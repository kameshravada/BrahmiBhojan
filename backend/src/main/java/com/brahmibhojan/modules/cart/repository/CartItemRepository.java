package com.brahmibhojan.modules.cart.repository;

import com.brahmibhojan.modules.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findAllByCartIdOrderByCreatedAtAsc(UUID cartId);

    Optional<CartItem> findByIdAndCartId(UUID itemId, UUID cartId);

    Optional<CartItem> findByCartIdAndProductVariantId(UUID cartId, UUID productVariantId);
}

