package com.brahmibhojan.modules.catalog.service;

import com.brahmibhojan.modules.catalog.model.Category;
import com.brahmibhojan.modules.catalog.model.Product;
import com.brahmibhojan.modules.catalog.model.ProductVariant;
import com.brahmibhojan.modules.catalog.repository.CategoryRepository;
import com.brahmibhojan.modules.catalog.repository.ProductRepository;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(prefix = "catalog.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class CatalogSeedDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category dals = createCategory("Dals & Pulses", "dals-pulses", 1);
        Category dryFruits = createCategory("Dry Fruits", "dry-fruits", 2);

        Product toorDal = createProduct(dals, "Toor Dal Premium", "toor-dal-premium");
        Product almonds = createProduct(dryFruits, "California Almond", "california-almond");

        createVariant(toorDal, "500 gm", "500", "gm", "1000", true, 1);
        createVariant(toorDal, "2 x 500 gm", "1000", "gm", "2000", false, 2);
        createVariant(toorDal, "5 kg", "5", "kg", "9300", false, 3);

        createVariant(almonds, "250 gm", "250", "gm", "850", true, 1);
        createVariant(almonds, "1 kg", "1", "kg", "3200", false, 2);

        log.info("Catalog seed data initialized with sample categories/products/variants");
    }

    private Category createCategory(String name, String slug, int sortOrder) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(name + " category");
        category.setSortOrder(sortOrder);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    private Product createProduct(Category category, String name, String slug) {
        Product product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setSlug(slug);
        product.setDescription(name + " high quality selection");
        product.setPrice(BigDecimal.ZERO);
        product.setUnit("variant");
        product.setAvailable(true);
        return productRepository.save(product);
    }

    private void createVariant(
            Product product,
            String label,
            String quantityValue,
            String quantityUnit,
            String price,
            boolean defaultVariant,
            int sortOrder
    ) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setLabel(label);
        variant.setQuantityValue(new BigDecimal(quantityValue));
        variant.setQuantityUnit(quantityUnit);
        variant.setPrice(new BigDecimal(price));
        variant.setMrp(new BigDecimal(price));
        variant.setDefaultVariant(defaultVariant);
        variant.setSortOrder(sortOrder);
        variant.setAvailable(true);
        productVariantRepository.save(variant);
    }
}

