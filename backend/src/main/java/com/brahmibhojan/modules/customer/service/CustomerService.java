package com.brahmibhojan.modules.customer.service;

import com.brahmibhojan.modules.customer.dto.CustomerAddressRequest;
import com.brahmibhojan.modules.customer.dto.CustomerAddressResponse;
import com.brahmibhojan.modules.customer.dto.CustomerProfileResponse;
import com.brahmibhojan.modules.customer.dto.UpdateCustomerProfileRequest;
import com.brahmibhojan.modules.customer.model.CustomerAddress;
import com.brahmibhojan.modules.customer.repository.CustomerAddressRepository;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final CustomerAddressRepository customerAddressRepository;


    public CustomerProfileResponse getMyProfile(String mobile) {
        User user = findByMobile(mobile);
        return toProfileResponse(user);
    }

    @Transactional
    public CustomerProfileResponse updateMyProfile(String mobile, UpdateCustomerProfileRequest request) {
        User user = findByMobile(mobile);

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }

        if (request.email() != null) {
            String normalizedEmail = request.email().trim().toLowerCase();
            if (!normalizedEmail.equals(user.getEmail()) && userRepository.existsByEmail(normalizedEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
            }
            user.setEmail(normalizedEmail);
        }

        if (request.mobile() != null && !request.mobile().isBlank()) {
            String normalizedMobile = request.mobile().replaceAll("[\\s-]", "").trim();
            if (!normalizedMobile.equals(user.getMobile()) && userRepository.existsByMobile(normalizedMobile)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number is already in use");
            }
            user.setMobile(normalizedMobile);
        }

        User savedUser = userRepository.save(user);
        return toProfileResponse(savedUser);
    }

    public List<CustomerAddressResponse> getMyAddresses(String mobile) {
        User user = findByMobile(mobile);
        return customerAddressRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toAddressResponse)
                .toList();
    }

    @Transactional
    public CustomerAddressResponse addAddress(String mobile, CustomerAddressRequest request) {
        User user = findByMobile(mobile);

        if (request.isDefault()) {
            clearExistingDefaultAddress(user.getId());
        }

        CustomerAddress address = new CustomerAddress();
        address.setUser(user);
        address.setRecipientName(request.recipientName().trim());
        address.setPhoneNumber(request.phoneNumber().trim());
        address.setLine1(request.line1().trim());
        address.setLine2(request.line2() == null ? null : request.line2().trim());
        address.setLandmark(request.landmark() == null ? null : request.landmark().trim());
        address.setCity(request.city().trim());
        address.setState(request.state().trim());
        address.setCountry(request.country().trim());
        address.setPostalCode(request.postalCode().trim());
        address.setDefault(request.isDefault());

        CustomerAddress saved = customerAddressRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional
    public CustomerAddressResponse updateAddress(String mobile, UUID addressId, CustomerAddressRequest request) {
        User user = findByMobile(mobile);
        CustomerAddress address = customerAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        if (request.isDefault()) {
            clearExistingDefaultAddress(user.getId());
        }

        address.setRecipientName(request.recipientName().trim());
        address.setPhoneNumber(request.phoneNumber().trim());
        address.setLine1(request.line1().trim());
        address.setLine2(request.line2() == null ? null : request.line2().trim());
        address.setLandmark(request.landmark() == null ? null : request.landmark().trim());
        address.setCity(request.city().trim());
        address.setState(request.state().trim());
        address.setCountry(request.country().trim());
        address.setPostalCode(request.postalCode().trim());
        address.setDefault(request.isDefault());

        CustomerAddress saved = customerAddressRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional
    public void deleteAddress(String mobile, UUID addressId) {
        User user = findByMobile(mobile);
        CustomerAddress address = customerAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));
        customerAddressRepository.delete(address);
    }

    private void clearExistingDefaultAddress(UUID userId) {
        customerAddressRepository.findByUserIdAndIsDefaultTrue(userId).ifPresent(existing -> {
            existing.setDefault(false);
            customerAddressRepository.save(existing);
        });
    }

    private User findByMobile(String mobile) {
        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private CustomerProfileResponse toProfileResponse(User user) {
        return new CustomerProfileResponse(
                user.getId(),
                user.getMobile(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileVerifiedAt(),
                user.getStatus().name()
        );
    }

    private CustomerAddressResponse toAddressResponse(CustomerAddress address) {
        return new CustomerAddressResponse(
                address.getId(),
                address.getRecipientName(),
                address.getPhoneNumber(),
                address.getLine1(),
                address.getLine2(),
                address.getLandmark(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode(),
                address.isDefault()
        );
    }
}

