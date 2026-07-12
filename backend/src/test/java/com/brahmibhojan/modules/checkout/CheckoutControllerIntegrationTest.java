package com.brahmibhojan.modules.checkout;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import com.brahmibhojan.modules.notifications.repository.NotificationEventRepository;
import com.brahmibhojan.modules.users.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private NotificationEventRepository notificationEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkoutValidateAndOrderCreateShouldSucceed() throws Exception {
        String mobile = "+919333222111";
        String accessToken = loginAndGetAccessToken(mobile);

        UUID variantId = productVariantRepository.findAll().stream()
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("variantId", variantId, "quantity", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(1));

        MvcResult addressResult = mockMvc.perform(post("/api/v1/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "recipientName", "BB User",
                                "phoneNumber", mobile,
                                "line1", "Street 1",
                                "line2", "Area",
                                "landmark", "Near Temple",
                                "city", "Hyderabad",
                                "state", "Telangana",
                                "country", "India",
                                "postalCode", "500001",
                                "isDefault", true
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> addressResponse = objectMapper.readValue(
                addressResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        String addressId = addressResponse.get("addressId").toString();

        mockMvc.perform(post("/api/v1/checkout/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("addressId", addressId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemCount").value(1))
                .andExpect(jsonPath("$.payableAmount").isNotEmpty());

        MvcResult orderCreateResult = mockMvc.perform(post("/api/v1/checkout/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "addressId", addressId,
                                "paymentMethod", "RAZORPAY",
                                "idempotencyKey", "order-test-001",
                                "notes", "Door delivery"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").isNotEmpty())
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andReturn();

        Map<String, Object> orderResponse = objectMapper.readValue(
                orderCreateResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        String orderNumber = orderResponse.get("orderNumber").toString();

        UUID userId = userRepository.findByMobile(mobile).orElseThrow().getId();
        var notifications = notificationEventRepository.findAllByUserIdAndTypeOrderByCreatedAtDesc(
                userId,
                NotificationType.ORDER_CONFIRMATION
        );
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.stream().anyMatch(event -> event.getMessage().contains(orderNumber))).isTrue();
    }

    private String loginAndGetAccessToken(String mobile) throws Exception {
        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "mobile", mobile,
                                "deviceId", "checkout-test-device"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Map<String, Object> otpResponse = objectMapper.readValue(
                otpRequestResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        MvcResult verifyResult = mockMvc.perform(post("/api/v1/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "challengeId", otpResponse.get("challengeId"),
                                "mobile", mobile,
                                "otp", otpResponse.get("otpPreview"),
                                "deviceId", "checkout-test-device"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> verifyResponse = objectMapper.readValue(
                verifyResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        return verifyResponse.get("accessToken").toString();
    }
}


