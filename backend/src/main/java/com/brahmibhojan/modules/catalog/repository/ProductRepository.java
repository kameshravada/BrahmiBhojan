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
            select p from Product p
            where p.available = true
              and (:categorySlug is null or p.category.slug = :categorySlug)
              and (:query is null or lower(p.name) like lower(concat('%', :query, '%'))
                   or lower(p.slug) like lower(concat('%', :query, '%')))
              and exists (
                  select 1 from ProductVariant v
                  where v.product = p
                    and v.available = true
                    and (:unit is null or lower(v.quantityUnit) = lower(:unit))
                    and (:minPrice is null or v.price >= :minPrice)
                    and (:maxPrice is null or v.price <= :maxPrice)
              )
            order by
               case
                   when :sort = 'relevance' and :query is not null and lower(p.name) = lower(:query) then 0
                   when :sort = 'relevance' and :query is not null and lower(p.slug) = lower(:query) then 1
                   when :sort = 'relevance' and :query is not null and lower(p.name) like lower(concat(:query, '%')) then 2
                   when :sort = 'relevance' and :query is not null and lower(p.slug) like lower(concat(:query, '%')) then 3
                   when :sort = 'relevance' and :query is not null and lower(p.name) like lower(concat('%', :query, '%')) then 4
                   when :sort = 'relevance' and :query is not null and lower(p.slug) like lower(concat('%', :query, '%')) then 5
                   else 99
               end asc,
               case
                   when :sort = 'price_asc' then (select min(v2.price) from ProductVariant v2 where v2.product = p and v2.available = true)
               end asc,
               case
                   when :sort = 'price_desc' then (select min(v3.price) from ProductVariant v3 where v3.product = p and v3.available = true)
               end desc,
               case
                   when :sort = 'newest' then p.createdAt
               end desc,
               p.createdAt desc,
               p.name asc
            """)
    Page<Product> searchProducts(
            @Param("categorySlug") String categorySlug,
            @Param("query") String query,
            @Param("unit") String unit,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("sort") String sort,
            Pageable pageable
    );
}

