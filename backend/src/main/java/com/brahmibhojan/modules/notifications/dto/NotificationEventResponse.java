package com.brahmibhojan.modules.notifications.dto;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.model.NotificationStatus;
import com.brahmibhojan.modules.notifications.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationEventResponse(
        UUID notificationId,
        NotificationType type,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String message,
        boolean marketing,
        Instant createdAt,
        Instant lastAttemptAt
) {
}

