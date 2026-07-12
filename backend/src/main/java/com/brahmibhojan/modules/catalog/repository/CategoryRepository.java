package com.brahmibhojan.modules.catalog.repository;

import com.brahmibhojan.modules.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByActiveTrueOrderBySortOrderAscNameAsc();

    Optional<Category> findBySlugAndActiveTrue(String slug);
}

