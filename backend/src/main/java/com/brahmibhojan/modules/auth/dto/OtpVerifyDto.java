package com.brahmibhojan.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpVerifyDto(
        @NotBlank(message = "Challenge id is required")
        String challengeId,

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
        String mobile,

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6 digit number")
        String otp,

        @Size(max = 120, message = "Device id must not exceed 120 characters")
        String deviceId
) {
}

