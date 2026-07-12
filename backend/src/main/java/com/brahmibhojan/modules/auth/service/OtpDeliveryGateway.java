package com.brahmibhojan.modules.auth.service;

public interface OtpDeliveryGateway {

    void sendOtp(String mobile, String otp);
}

