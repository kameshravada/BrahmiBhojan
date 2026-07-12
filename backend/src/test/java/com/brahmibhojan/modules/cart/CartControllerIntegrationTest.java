package com.brahmibhojan.modules.cart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    void guestCartShouldMergeAfterOtpLogin() throws Exception {
        UUID variantId = productVariantRepository.findAll().stream()
                .findFirst()
                .orElseThrow()
                .getId();

        String guestToken = "guest-token-merge-test";

        Map<String, Object> addItemPayload = Map.of(
                "variantId", variantId,
                "quantity", 2
        );

        mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(1));

        Map<String, Object> otpRequestPayload = Map.of(
                "mobile", "+919555444333",
                "deviceId", "test-device-1"
        );

        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otpRequestPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        Map<String, Object> verifyOtpPayload = Map.of(
                "challengeId", otpResponse.get("challengeId"),
                "mobile", "+919555444333",
                "otp", otpResponse.get("otpPreview"),
                "deviceId", "test-device-1"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyOtpPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        Map<String, Object> loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String accessToken = loginResponse.get("accessToken").toString();

        mockMvc.perform(post("/api/v1/cart/merge")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("guestToken", guestToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(1));
    }

    @Test
    void cartItemQuantityUpdateShouldFailWhenRequestedQtyExceedsInventory() throws Exception {
        UUID variantId = productVariantRepository.findAll().stream()
                .findFirst()
                .orElseThrow()
                .getId();

        String guestToken = "guest-token-inventory-check";

        MvcResult addResult = mockMvc.perform(post("/api/v1/cart/items")
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "variantId", variantId,
                                "quantity", 1
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> addResponse = objectMapper.readValue(
                addResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        Map<String, Object> item = ((java.util.List<Map<String, Object>>) addResponse.get("items")).getFirst();
        String itemId = item.get("itemId").toString();

        mockMvc.perform(patch("/api/v1/cart/items/{itemId}", itemId)
                        .header("X-Guest-Token", guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 101))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Only 100 units available")));
    }
}


