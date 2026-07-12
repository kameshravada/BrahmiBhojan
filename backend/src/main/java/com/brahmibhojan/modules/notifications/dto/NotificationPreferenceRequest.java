package com.brahmibhojan.modules.notifications.dto;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import jakarta.validation.constraints.NotNull;

public record NotificationPreferenceRequest(
        @NotNull NotificationChannel channel,
        boolean marketingEnabled
) {
}

