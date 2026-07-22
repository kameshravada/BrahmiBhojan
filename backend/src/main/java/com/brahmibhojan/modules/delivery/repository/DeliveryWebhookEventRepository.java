package com.brahmibhojan.modules.delivery.repository;

import com.brahmibhojan.modules.delivery.model.DeliveryWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryWebhookEventRepository extends JpaRepository<DeliveryWebhookEvent, UUID> {

    Optional<DeliveryWebhookEvent> findByExternalEventId(String externalEventId);
}

