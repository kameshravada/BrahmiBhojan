package com.brahmibhojan.modules.notifications.gateway;

public interface SmsGateway {

    String sendSms(String mobile, String message);
}

