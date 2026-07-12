package com.brahmibhojan.modules.customer.dto;

import java.util.UUID;

public record CustomerAddressResponse(
        UUID addressId,
        String recipientName,
        String phoneNumber,
        String line1,
        String line2,
        String landmark,
        String city,
        String state,
        String country,
        String postalCode,
        boolean isDefault
) {
}

