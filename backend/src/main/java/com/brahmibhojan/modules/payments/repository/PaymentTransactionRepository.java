package com.brahmibhojan.modules.payments.repository;

import com.brahmibhojan.modules.payments.model.PaymentTransaction;
import com.brahmibhojan.modules.payments.model.PaymentTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);

    Optional<PaymentTransaction> findByOrderId(UUID orderId);

    List<PaymentTransaction> findAllByStatusAndCreatedAtBefore(PaymentTransactionStatus status, Instant createdAt);
}

