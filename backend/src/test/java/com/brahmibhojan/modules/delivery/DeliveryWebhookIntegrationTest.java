package com.brahmibhojan.modules.delivery;

import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import com.brahmibhojan.modules.notifications.repository.NotificationEventRepository;
import com.brahmibhojan.modules.orders.model.Order;
import com.brahmibhojan.modules.orders.model.OrderStatus;
import com.brahmibhojan.modules.orders.repository.OrderRepository;
import com.brahmibhojan.modules.users.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryWebhookIntegrationTest {

    private static final int ORDER_QUANTITY = 2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationEventRepository notificationEventRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID variantId;

    @BeforeEach
    void setUp() {
        variantId = productVariantRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    @Test
    void deliveryWebhookShouldSetShippedAndQueueNotification() throws Exception {
        String mobile = "+919333111001";
        String accessToken = loginAndGetAccessToken(mobile);
        String orderId = createPaidOrder(accessToken, mobile, "delivery-status-1", "evt-payment-1");

        Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        String eventId = "del-evt-1001";
        String partner = "DELHIVERY";
        String providerStatus = "in_transit";
        String signature = signDeliveryWebhook(partner + ":" + eventId + ":" + order.getOrderNumber() + ":" + providerStatus,
                "test-delivery-signature-secret");

        mockMvc.perform(post("/api/v1/delivery/webhook")
                        .header("X-Delivery-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "partner", partner,
                                "eventId", eventId,
                                "orderNumber", order.getOrderNumber(),
                                "trackingId", "DL-TRACK-1001",
                                "status", providerStatus,
                                "remark", "Package in transit"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"))
                .andExpect(jsonPath("$.normalizedStatus").value("SHIPPED"));

        Order updatedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        UUID userId = userRepository.findByMobile(mobile).orElseThrow().getId();
        var notifications = notificationEventRepository.findAllByUserIdAndTypeOrderByCreatedAtDesc(
                userId,
                NotificationType.ORDER_SHIPPED
        );
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.stream().anyMatch(event -> event.getMessage().contains(order.getOrderNumber()))).isTrue();
    }

    @Test
    void deliveryWebhookShouldMapDelhiveryDispatchedStatus() throws Exception {
        String mobile = "+919333111004";
        String accessToken = loginAndGetAccessToken(mobile);
        String orderId = createPaidOrder(accessToken, mobile, "delivery-status-4", "evt-payment-4");

        Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        String eventId = "del-evt-4001";
        String partner = "DELHIVERY";
        String providerStatus = "shipment_dispatched";
        String signature = signDeliveryWebhook(partner + ":" + eventId + ":" + order.getOrderNumber() + ":" + providerStatus,
                "test-delivery-signature-secret");

        mockMvc.perform(post("/api/v1/delivery/webhook")
                        .header("X-Delivery-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "partner", partner,
                                "eventId", eventId,
                                "orderNumber", order.getOrderNumber(),
                                "trackingId", "DL-TRACK-4001",
                                "status", providerStatus,
                                "remark", "Shipment dispatched from origin hub"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"))
                .andExpect(jsonPath("$.normalizedStatus").value("SHIPPED"));

        Order updatedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void deliveryWebhookShouldIgnoreDuplicateEvents() throws Exception {
        String mobile = "+919333111002";
        String accessToken = loginAndGetAccessToken(mobile);
        String orderId = createPaidOrder(accessToken, mobile, "delivery-status-2", "evt-payment-2");
        Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();

        String eventId = "del-evt-2001";
        String partner = "DELHIVERY";
        String providerStatus = "out_for_delivery";
        String signature = signDeliveryWebhook(partner + ":" + eventId + ":" + order.getOrderNumber() + ":" + providerStatus,
                "test-delivery-signature-secret");

        String payload = objectMapper.writeValueAsString(Map.of(
                "partner", partner,
                "eventId", eventId,
                "orderNumber", order.getOrderNumber(),
                "trackingId", "DL-TRACK-2001",
                "status", providerStatus,
                "remark", "Rider assigned"
        ));

        mockMvc.perform(post("/api/v1/delivery/webhook")
                        .header("X-Delivery-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"));

        mockMvc.perform(post("/api/v1/delivery/webhook")
                        .header("X-Delivery-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("IGNORED_DUPLICATE"));
    }

    @Test
    void deliveryWebhookShouldRejectInvalidSignature() throws Exception {
        String mobile = "+919333111003";
        String accessToken = loginAndGetAccessToken(mobile);
        String orderId = createPaidOrder(accessToken, mobile, "delivery-status-3", "evt-payment-3");
        Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();

        mockMvc.perform(post("/api/v1/delivery/webhook")
                        .header("X-Delivery-Signature", "invalid-signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "partner", "DELHIVERY",
                                "eventId", "del-evt-3001",
                                "orderNumber", order.getOrderNumber(),
                                "trackingId", "DL-TRACK-3001",
                                "status", "delivered",
                                "remark", "Delivered"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    private String createPaidOrder(String accessToken, String mobile, String idempotencyKey, String paymentEventId) throws Exception {
        String orderId = createCheckoutOrder(accessToken, mobile, idempotencyKey);
        Map<String, Object> paymentOrderResponse = createPaymentOrder(accessToken, orderId);
        String providerOrderId = paymentOrderResponse.get("providerOrderId").toString();
        String paymentStatus = "paid";
        String signature = signPaymentWebhook(paymentEventId + ":" + providerOrderId + ":" + paymentStatus,
                "test-signature-secret");

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", paymentEventId,
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_" + paymentEventId,
                                "status", paymentStatus,
                                "amount", paymentOrderResponse.get("amount"),
                                "currency", "INR"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"));

        return orderId;
    }

    private String createCheckoutOrder(String accessToken, String mobile, String idempotencyKey) throws Exception {
        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("variantId", variantId, "quantity", ORDER_QUANTITY))))
                .andExpect(status().isOk());

        String addressId = createAddress(accessToken, mobile);

        MvcResult orderResult = mockMvc.perform(post("/api/v1/checkout/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "addressId", addressId,
                                "paymentMethod", "RAZORPAY",
                                "idempotencyKey", idempotencyKey
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> orderResponse = objectMapper.readValue(
                orderResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        return orderResponse.get("orderId").toString();
    }

    private Map<String, Object> createPaymentOrder(String accessToken, String orderId) throws Exception {
        MvcResult paymentOrderResult = mockMvc.perform(post("/api/v1/payments/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("orderId", orderId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("RAZORPAY"))
                .andReturn();

        return objectMapper.readValue(
                paymentOrderResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
    }

    private String loginAndGetAccessToken(String mobile) throws Exception {
        MvcResult otpRequestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "mobile", mobile,
                                "deviceId", "delivery-test-device"
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
                                "deviceId", "delivery-test-device"
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

    private String createAddress(String accessToken, String mobile) throws Exception {
        MvcResult addressResult = mockMvc.perform(post("/api/v1/me/addresses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "recipientName", "Delivery User",
                                "phoneNumber", mobile,
                                "line1", "Street 1",
                                "line2", "Area",
                                "landmark", "Near Lake",
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

        return addressResponse.get("addressId").toString();
    }

    private String signPaymentWebhook(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }

    private String signDeliveryWebhook(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}

