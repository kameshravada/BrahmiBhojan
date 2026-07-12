package com.brahmibhojan.modules.notifications.service;

import com.brahmibhojan.modules.notifications.dto.NotificationEventResponse;
import com.brahmibhojan.modules.notifications.dto.NotificationPreferenceRequest;
import com.brahmibhojan.modules.notifications.dto.NotificationPreferenceResponse;
import com.brahmibhojan.modules.notifications.model.NotificationPreference;
import com.brahmibhojan.modules.notifications.repository.NotificationEventRepository;
import com.brahmibhojan.modules.notifications.repository.NotificationPreferenceRepository;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.model.UserStatus;
import com.brahmibhojan.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationCenterService {

    private final UserRepository userRepository;
    private final NotificationEventRepository notificationEventRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Transactional(readOnly = true)
    public List<NotificationEventResponse> getMyNotifications(String mobile) {
        User user = findActiveUser(mobile);
        return notificationEventRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(event -> new NotificationEventResponse(
                        event.getId(),
                        event.getType(),
                        event.getChannel(),
                        event.getStatus(),
                        event.getSubject(),
                        event.getMessage(),
                        event.isMarketing(),
                        event.getCreatedAt(),
                        event.getLastAttemptAt()
                ))
                .toList();
    }

    @Transactional
    public NotificationPreferenceResponse updatePreference(String mobile, NotificationPreferenceRequest request) {
        User user = findActiveUser(mobile);

        NotificationPreference preference = notificationPreferenceRepository
                .findByUserIdAndChannel(user.getId(), request.channel())
                .orElseGet(() -> {
                    NotificationPreference created = new NotificationPreference();
                    created.setUserId(user.getId());
                    created.setChannel(request.channel());
                    return created;
                });

        preference.setMarketingEnabled(request.marketingEnabled());
        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        return new NotificationPreferenceResponse(saved.getChannel(), saved.isMarketingEnabled());
    }

    private User findActiveUser(String mobile) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        return user;
    }
}

