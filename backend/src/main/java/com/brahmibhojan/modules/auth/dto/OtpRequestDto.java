package com.brahmibhojan.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtpRequestDto(
        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
        String mobile,

        @Size(max = 120, message = "Device id must not exceed 120 characters")
        String deviceId
) {
}

