package com.brahmibhojan.modules.notifications.repository;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.model.NotificationTemplate;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByTypeAndChannelAndActiveTrue(NotificationType type, NotificationChannel channel);
}

