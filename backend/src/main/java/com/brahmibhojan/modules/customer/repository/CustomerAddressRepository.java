package com.brahmibhojan.modules.customer.repository;

import com.brahmibhojan.modules.customer.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

    List<CustomerAddress> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<CustomerAddress> findByIdAndUserId(UUID addressId, UUID userId);

    Optional<CustomerAddress> findByUserIdAndIsDefaultTrue(UUID userId);
}

