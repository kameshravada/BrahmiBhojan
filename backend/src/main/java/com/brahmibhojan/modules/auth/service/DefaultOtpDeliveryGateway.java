package com.brahmibhojan.modules.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultOtpDeliveryGateway implements OtpDeliveryGateway {

    private static final Logger log = LoggerFactory.getLogger(DefaultOtpDeliveryGateway.class);

    @Override
    public void sendOtp(String mobile, String otp) {
        String maskedMobile = mobile.length() <= 4
                ? "****"
                : "****" + mobile.substring(mobile.length() - 4);
        // OTP itself is intentionally not logged.
        log.info("OTP challenge sent for mobile {}", maskedMobile);
    }
}

