package com.brahmibhojan.modules.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerProfileRequest(
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String fullName,

        @Email(message = "Email must be valid")
        @Size(max = 120, message = "Email must not exceed 120 characters")
        String email,

        @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
        String mobile
) {
}

