package com.brahmibhojan.modules.notifications.dto;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;

public record NotificationPreferenceResponse(
        NotificationChannel channel,
        boolean marketingEnabled
) {
}

