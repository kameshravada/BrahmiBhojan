package com.brahmibhojan.modules.delivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "delivery_webhook_events")
@Getter
@Setter
public class DeliveryWebhookEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "external_event_id", nullable = false, unique = true, length = 140)
    private String externalEventId;

    @Column(name = "partner", nullable = false, length = 40)
    private String partner;

    @Column(name = "order_number", nullable = false, length = 40)
    private String orderNumber;

    @Column(name = "tracking_id", length = 120)
    private String trackingId;

    @Column(name = "provider_status", nullable = false, length = 60)
    private String providerStatus;

    @Column(name = "normalized_status", nullable = false, length = 30)
    private String normalizedStatus;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }
}

