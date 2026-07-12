package com.brahmibhojan.modules.payments.model;

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
@Table(name = "payment_webhook_events")
@Getter
@Setter
public class PaymentWebhookEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_event_id", nullable = false, unique = true, length = 120)
    private String providerEventId;

    @Column(name = "provider_order_id", nullable = false, length = 120)
    private String providerOrderId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

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

