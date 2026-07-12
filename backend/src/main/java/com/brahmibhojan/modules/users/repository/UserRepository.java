package com.brahmibhojan.modules.users.repository;

import com.brahmibhojan.modules.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMobile(String mobile);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);
}

