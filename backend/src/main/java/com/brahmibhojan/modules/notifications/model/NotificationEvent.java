package com.brahmibhojan.modules.notifications.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_events")
@Getter
@Setter
public class NotificationEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "recipient_mobile", length = 16)
    private String recipientMobile;

    @Column(name = "recipient_email", length = 120)
    private String recipientEmail;

    @Column(name = "subject", length = 180)
    private String subject;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 180)
    private String idempotencyKey;

    @Column(name = "is_marketing", nullable = false)
    private boolean marketing;

    @Column(name = "provider_message_id", length = 120)
    private String providerMessageId;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}

