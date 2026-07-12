package com.brahmibhojan.modules.notifications.gateway.impl;

import com.brahmibhojan.modules.notifications.gateway.EmailGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DefaultEmailGateway implements EmailGateway {

    @Override
    public String sendEmail(String email, String subject, String message) {
        String messageId = "mail-" + UUID.randomUUID();
        log.info("Email queued email={} messageId={} subject={} payload={}", maskEmail(email), messageId, subject, message);
        return messageId;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@", 2);
        String prefix = parts[0].length() <= 2 ? "**" : parts[0].substring(0, 2) + "***";
        return prefix + "@" + parts[1];
    }
}

