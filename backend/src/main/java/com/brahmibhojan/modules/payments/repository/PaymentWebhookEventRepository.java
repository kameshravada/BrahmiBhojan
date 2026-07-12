package com.brahmibhojan.modules.payments.repository;

import com.brahmibhojan.modules.payments.model.PaymentWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentWebhookEventRepository extends JpaRepository<PaymentWebhookEvent, UUID> {

    Optional<PaymentWebhookEvent> findByProviderEventId(String providerEventId);
}

