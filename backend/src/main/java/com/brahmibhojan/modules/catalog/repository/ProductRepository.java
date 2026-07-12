package com.brahmibhojan.modules.catalog.repository;

import com.brahmibhojan.modules.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByAvailableTrue(Pageable pageable);

    Page<Product> findAllByCategorySlugAndAvailableTrue(String categorySlug, Pageable pageable);

    Page<Product> findAllByNameContainingIgnoreCaseAndAvailableTrue(String query, Pageable pageable);

    Optional<Product> findBySlugAndAvailableTrue(String slug);

    @Query("""
            select distinct p from Product p
            join p.variants v
            where p.available = true
              and v.available = true
              and (:categorySlug is null or p.category.slug = :categorySlug)
              and (:query is null or lower(p.name) like lower(concat('%', :query, '%'))
                   or lower(p.slug) like lower(concat('%', :query, '%')))
              and (:unit is null or lower(v.quantityUnit) = lower(:unit))
              and (:minPrice is null or v.price >= :minPrice)
              and (:maxPrice is null or v.price <= :maxPrice)
            """)
    Page<Product> searchProducts(
            @Param("categorySlug") String categorySlug,
            @Param("query") String query,
            @Param("unit") String unit,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}

