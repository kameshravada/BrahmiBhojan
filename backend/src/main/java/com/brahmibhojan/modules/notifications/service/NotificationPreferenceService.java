package com.brahmibhojan.modules.notifications.service;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public boolean isMarketingAllowed(UUID userId, NotificationChannel channel) {
        if (userId == null) {
            return false;
        }
        return notificationPreferenceRepository.findByUserIdAndChannel(userId, channel)
                .map(preference -> preference.isMarketingEnabled())
                .orElse(false);
    }
}

