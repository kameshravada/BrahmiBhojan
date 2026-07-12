package com.brahmibhojan.modules.payments;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.brahmibhojan.modules.catalog.repository.ProductVariantRepository;
import com.brahmibhojan.modules.inventory.repository.InventoryStockRepository;
import com.brahmibhojan.modules.orders.model.Order;
import com.brahmibhojan.modules.orders.model.OrderStatus;
import com.brahmibhojan.modules.orders.model.PaymentStatus;
import com.brahmibhojan.modules.orders.repository.OrderRepository;
import com.brahmibhojan.modules.payments.repository.PaymentTransactionRepository;
import com.brahmibhojan.modules.payments.service.PaymentService;
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
import java.time.Instant;
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
class PaymentWorkflowIntegrationTest {

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
    private InventoryStockRepository inventoryStockRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentService paymentService;

    private UUID variantId;

    @BeforeEach
    void setUp() {
        variantId = productVariantRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    @Test
    void paymentWebhookShouldConfirmOrderAndConsumeReservation() throws Exception {
        String accessToken = loginAndGetAccessToken("+919222111000");

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("variantId", variantId, "quantity", ORDER_QUANTITY))))
                .andExpect(status().isOk());

        String addressId = createAddress(accessToken, "+919222111000");

        MvcResult orderResult = mockMvc.perform(post("/api/v1/checkout/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "addressId", addressId,
                                "paymentMethod", "RAZORPAY",
                                "idempotencyKey", "payment-workflow-1"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> orderResponse = objectMapper.readValue(
                orderResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String orderId = orderResponse.get("orderId").toString();

        MvcResult paymentOrderResult = mockMvc.perform(post("/api/v1/payments/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("orderId", orderId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("RAZORPAY"))
                .andReturn();

        Map<String, Object> paymentOrderResponse = objectMapper.readValue(
                paymentOrderResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String providerOrderId = paymentOrderResponse.get("providerOrderId").toString();
        String eventId = "evt-1001";
        String paymentStatus = "paid";
        var stockBeforeWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        String signature = signWebhook(eventId + ":" + providerOrderId + ":" + paymentStatus, "test-signature-secret");

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", eventId,
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_123",
                                "status", paymentStatus,
                                "amount", paymentOrderResponse.get("amount"),
                                "currency", "INR"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"));

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", eventId,
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_123",
                                "status", paymentStatus,
                                "amount", paymentOrderResponse.get("amount"),
                                "currency", "INR"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("IGNORED_DUPLICATE"));

        Order savedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(savedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);

        var stockAfterWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        assertThat(stockAfterWebhook.getAvailableQuantity()).isEqualTo(stockBeforeWebhook.getAvailableQuantity() - ORDER_QUANTITY);
        assertThat(stockAfterWebhook.getReservedQuantity()).isEqualTo(stockBeforeWebhook.getReservedQuantity() - ORDER_QUANTITY);
    }

    @Test
    void paymentWebhookShouldRejectInvalidSignature() throws Exception {
        String accessToken = loginAndGetAccessToken("+919222111010");
        String orderId = createCheckoutOrder(accessToken, "+919222111010", "payment-workflow-invalid-signature");
        String providerOrderId = createPaymentOrder(accessToken, orderId).get("providerOrderId").toString();
        var stockBeforeWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Signature", "invalid-signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", "evt-invalid-1",
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_invalid",
                                "status", "paid",
                                "amount", 2000.0,
                                "currency", "INR"
                        ))))
                .andExpect(status().isUnauthorized());

        Order savedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(savedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

        var stockAfterWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        assertThat(stockAfterWebhook.getAvailableQuantity()).isEqualTo(stockBeforeWebhook.getAvailableQuantity());
        assertThat(stockAfterWebhook.getReservedQuantity()).isEqualTo(stockBeforeWebhook.getReservedQuantity());
    }

    @Test
    void paymentWebhookShouldRejectMissingSignatureHeader() throws Exception {
        String accessToken = loginAndGetAccessToken("+919222111030");
        String orderId = createCheckoutOrder(accessToken, "+919222111030", "payment-workflow-missing-signature");
        Map<String, Object> paymentOrderResponse = createPaymentOrder(accessToken, orderId);
        String providerOrderId = paymentOrderResponse.get("providerOrderId").toString();

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", "evt-missing-signature-1",
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_missing_sig",
                                "status", "paid",
                                "amount", paymentOrderResponse.get("amount"),
                                "currency", "INR"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void paymentWebhookFailedShouldCancelOrderAndReleaseReservation() throws Exception {
        String accessToken = loginAndGetAccessToken("+919222111020");
        String orderId = createCheckoutOrder(accessToken, "+919222111020", "payment-workflow-failed-status");
        Map<String, Object> paymentOrderResponse = createPaymentOrder(accessToken, orderId);
        String providerOrderId = paymentOrderResponse.get("providerOrderId").toString();

        String eventId = "evt-failed-1";
        String paymentStatus = "failed";
        var stockBeforeWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        String signature = signWebhook(eventId + ":" + providerOrderId + ":" + paymentStatus, "test-signature-secret");

        mockMvc.perform(post("/api/v1/payments/webhook")
                        .header("X-Payment-Signature", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "eventId", eventId,
                                "providerOrderId", providerOrderId,
                                "providerPaymentId", "pay_failed",
                                "status", paymentStatus,
                                "amount", paymentOrderResponse.get("amount"),
                                "currency", "INR"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PROCESSED"));

        Order savedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(savedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

        var stockAfterWebhook = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        assertThat(stockAfterWebhook.getAvailableQuantity()).isEqualTo(stockBeforeWebhook.getAvailableQuantity());
        assertThat(stockAfterWebhook.getReservedQuantity()).isEqualTo(stockBeforeWebhook.getReservedQuantity() - ORDER_QUANTITY);
    }

    @Test
    void staleCreatedPaymentShouldBeReconciledAsFailedAndReleaseInventory() throws Exception {
        String accessToken = loginAndGetAccessToken("+919222111040");
        String orderId = createCheckoutOrder(accessToken, "+919222111040", "payment-workflow-stale-reconcile");
        String providerOrderId = createPaymentOrder(accessToken, orderId).get("providerOrderId").toString();

        var transaction = paymentTransactionRepository.findByProviderOrderId(providerOrderId).orElseThrow();
        transaction.setCreatedAt(Instant.now().minusSeconds(120));
        paymentTransactionRepository.save(transaction);

        var stockBeforeReconcile = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        int reconciledCount = paymentService.reconcileStaleCreatedTransactions();
        assertThat(reconciledCount).isGreaterThanOrEqualTo(1);

        Order savedOrder = orderRepository.findById(UUID.fromString(orderId)).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(savedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

        var updatedTransaction = paymentTransactionRepository.findByProviderOrderId(providerOrderId).orElseThrow();
        assertThat(updatedTransaction.getStatus().name()).isEqualTo("FAILED");

        var stockAfterReconcile = inventoryStockRepository.findByVariantId(variantId).orElseThrow();
        assertThat(stockAfterReconcile.getAvailableQuantity()).isEqualTo(stockBeforeReconcile.getAvailableQuantity());
        assertThat(stockAfterReconcile.getReservedQuantity())
                .isLessThanOrEqualTo(stockBeforeReconcile.getReservedQuantity() - ORDER_QUANTITY);
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
                                "deviceId", "payment-test-device"
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
                                "deviceId", "payment-test-device"
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
                                "recipientName", "BB User",
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

    private String signWebhook(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}

