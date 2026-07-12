package com.brahmibhojan.modules.catalog;

import com.brahmibhojan.modules.catalog.model.Category;
import com.brahmibhojan.modules.catalog.model.Product;
import com.brahmibhojan.modules.catalog.model.ProductVariant;
import com.brahmibhojan.modules.catalog.repository.CategoryRepository;
import com.brahmibhojan.modules.catalog.repository.ProductRepository;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    void catalogReadApisShouldBePublicAndReturnEmptyState() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void productsShouldSupportRelevanceSort() throws Exception {
        createProductWithDefaultVariant("Premium Delight Dal", "premium-delight-dal", "900");

        mockMvc.perform(get("/api/v1/catalog/products")
                        .param("query", "premium")
                        .param("sort", "relevance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Premium Delight Dal"));
    }

    @Test
    void productsShouldSupportPriceSorts() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/products")
                        .param("sort", "price_asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("California Almond"));

        mockMvc.perform(get("/api/v1/catalog/products")
                        .param("sort", "price_desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Toor Dal Premium"));
    }

    private void createProductWithDefaultVariant(String name, String slugBase, String price) {
        Category category = categoryRepository.findBySlugAndActiveTrue("dals-pulses").orElseThrow();

        Product product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setSlug(slugBase + "-" + System.nanoTime());
        product.setDescription(name + " test data");
        product.setPrice(BigDecimal.ZERO);
        product.setUnit("variant");
        product.setAvailable(true);
        Product savedProduct = productRepository.save(product);

        ProductVariant variant = new ProductVariant();
        variant.setProduct(savedProduct);
        variant.setLabel("1 kg");
        variant.setQuantityValue(BigDecimal.ONE);
        variant.setQuantityUnit("kg");
        variant.setPrice(new BigDecimal(price));
        variant.setMrp(new BigDecimal(price));
        variant.setSortOrder(1);
        variant.setDefaultVariant(true);
        variant.setAvailable(true);
        productVariantRepository.save(variant);
    }
}

