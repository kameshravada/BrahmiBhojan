package com.brahmibhojan.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerAddressRequest(
        @NotBlank(message = "Recipient name is required")
        @Size(max = 100, message = "Recipient name must not exceed 100 characters")
        String recipientName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid phone number format")
        String phoneNumber,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 180, message = "Address line 1 must not exceed 180 characters")
        String line1,

        @Size(max = 180, message = "Address line 2 must not exceed 180 characters")
        String line2,

        @Size(max = 120, message = "Landmark must not exceed 120 characters")
        String landmark,

        @NotBlank(message = "City is required")
        @Size(max = 80, message = "City must not exceed 80 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 80, message = "State must not exceed 80 characters")
        String state,

        @NotBlank(message = "Country is required")
        @Size(max = 80, message = "Country must not exceed 80 characters")
        String country,

        @NotBlank(message = "Postal code is required")
        @Size(max = 12, message = "Postal code must not exceed 12 characters")
        String postalCode,

        boolean isDefault
) {
}

