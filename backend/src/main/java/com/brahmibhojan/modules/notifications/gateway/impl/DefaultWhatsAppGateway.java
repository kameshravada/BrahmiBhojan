package com.brahmibhojan.modules.notifications.gateway.impl;

import com.brahmibhojan.modules.notifications.gateway.WhatsAppGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DefaultWhatsAppGateway implements WhatsAppGateway {

    @Override
    public String sendWhatsApp(String mobile, String message) {
        String messageId = "wa-" + UUID.randomUUID();
        log.info("WhatsApp queued mobile={} messageId={} payload={}", maskMobile(mobile), messageId, message);
        return messageId;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "****";
        }
        return "****" + mobile.substring(mobile.length() - 4);
    }
}

