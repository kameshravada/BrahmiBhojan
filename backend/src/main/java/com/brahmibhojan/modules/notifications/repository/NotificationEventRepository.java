package com.brahmibhojan.modules.notifications.repository;

import com.brahmibhojan.modules.notifications.model.NotificationEvent;
import com.brahmibhojan.modules.notifications.model.NotificationStatus;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    Optional<NotificationEvent> findByIdempotencyKey(String idempotencyKey);

    List<NotificationEvent> findAllByStatusInAndNextRetryAtBefore(Collection<NotificationStatus> statuses, Instant nextRetryAt);

    List<NotificationEvent> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<NotificationEvent> findAllByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, NotificationType type);
}

