package com.brahmibhojan.modules.notifications.controller;

import com.brahmibhojan.modules.notifications.dto.NotificationEventResponse;
import com.brahmibhojan.modules.notifications.dto.NotificationPreferenceRequest;
import com.brahmibhojan.modules.notifications.dto.NotificationPreferenceResponse;
import com.brahmibhojan.modules.notifications.service.NotificationCenterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCenterService notificationCenterService;

    @GetMapping("/api/v1/me/notifications")
    public List<NotificationEventResponse> getMyNotifications(Authentication authentication) {
        return notificationCenterService.getMyNotifications(authentication.getName());
    }

    @PatchMapping("/api/v1/me/notifications/preferences")
    public NotificationPreferenceResponse updatePreference(
            Authentication authentication,
            @Valid @RequestBody NotificationPreferenceRequest request
    ) {
        return notificationCenterService.updatePreference(authentication.getName(), request);
    }
}

