package com.brahmibhojan.modules.customer.controller;

import com.brahmibhojan.modules.customer.dto.CustomerAddressRequest;
import com.brahmibhojan.modules.customer.dto.CustomerAddressResponse;
import com.brahmibhojan.modules.customer.dto.CustomerProfileResponse;
import com.brahmibhojan.modules.customer.dto.UpdateCustomerProfileRequest;
import com.brahmibhojan.modules.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/api/v1/me/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAddressResponse addAddress(
            Authentication authentication,
            @Valid @RequestBody CustomerAddressRequest request
    ) {
        return customerService.addAddress(authentication.getName(), request);
    }

    @PatchMapping("/api/v1/me")
    public CustomerProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateCustomerProfileRequest request
    ) {
        return customerService.updateMyProfile(authentication.getName(), request);
    }

    @PutMapping("/api/v1/me/addresses/{addressId}")
    public CustomerAddressResponse updateAddress(
            Authentication authentication,
            @PathVariable UUID addressId,
            @Valid @RequestBody CustomerAddressRequest request
    ) {
        return customerService.updateAddress(authentication.getName(), addressId, request);
    }

    @GetMapping("/api/v1/me")
    public CustomerProfileResponse myProfile(Authentication authentication) {
        return customerService.getMyProfile(authentication.getName());
    }

    @GetMapping("/api/v1/me/addresses")
    public List<CustomerAddressResponse> myAddresses(Authentication authentication) {
        return customerService.getMyAddresses(authentication.getName());
    }

    @DeleteMapping("/api/v1/me/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(Authentication authentication, @PathVariable UUID addressId) {
        customerService.deleteAddress(authentication.getName(), addressId);
    }
}

