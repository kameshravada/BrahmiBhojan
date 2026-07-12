package com.brahmibhojan.modules.notifications.gateway;

public interface EmailGateway {

    String sendEmail(String email, String subject, String message);
}

