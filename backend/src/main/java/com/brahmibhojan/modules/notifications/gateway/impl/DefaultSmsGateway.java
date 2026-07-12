package com.brahmibhojan.modules.notifications.gateway.impl;

import com.brahmibhojan.modules.notifications.gateway.SmsGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DefaultSmsGateway implements SmsGateway {

    @Override
    public String sendSms(String mobile, String message) {
        String messageId = "sms-" + UUID.randomUUID();
        log.info("SMS queued mobile={} messageId={} payload={}", maskMobile(mobile), messageId, message);
        return messageId;
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "****";
        }
        return "****" + mobile.substring(mobile.length() - 4);
    }
}

