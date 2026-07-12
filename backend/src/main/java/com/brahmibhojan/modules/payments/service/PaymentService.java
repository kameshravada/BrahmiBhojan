package com.brahmibhojan.modules.payments.service;

import com.brahmibhojan.modules.inventory.service.InventoryService;
import com.brahmibhojan.modules.orders.model.Order;
import com.brahmibhojan.modules.orders.model.OrderStatus;
import com.brahmibhojan.modules.orders.model.PaymentStatus;
import com.brahmibhojan.modules.orders.repository.OrderRepository;
import com.brahmibhojan.modules.payments.dto.CreatePaymentOrderRequest;
import com.brahmibhojan.modules.payments.dto.CreatePaymentOrderResponse;
import com.brahmibhojan.modules.payments.dto.PaymentWebhookRequest;
import com.brahmibhojan.modules.payments.dto.PaymentWebhookResponse;
import com.brahmibhojan.modules.payments.model.PaymentProvider;
import com.brahmibhojan.modules.payments.model.PaymentTransaction;
import com.brahmibhojan.modules.payments.model.PaymentTransactionStatus;
import com.brahmibhojan.modules.payments.model.PaymentWebhookEvent;
import com.brahmibhojan.modules.payments.repository.PaymentTransactionRepository;
import com.brahmibhojan.modules.payments.repository.PaymentWebhookEventRepository;
import com.brahmibhojan.modules.users.model.User;
import com.brahmibhojan.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String CURRENCY_INR = "INR";

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentWebhookEventRepository paymentWebhookEventRepository;
    private final InventoryService inventoryService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${payment.webhook.signature-secret:test-signature-secret}")
    private String webhookSignatureSecret;

    @Transactional
    public CreatePaymentOrderResponse createPaymentOrder(String mobile, CreatePaymentOrderRequest request) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create payment for another user order");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already paid");
        }

        PaymentTransaction existing = paymentTransactionRepository.findByOrderId(order.getId()).orElse(null);
        if (existing != null && existing.getStatus() == PaymentTransactionStatus.CREATED) {
            return toCreatePaymentOrderResponse(order, existing);
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setProvider(PaymentProvider.RAZORPAY);
        transaction.setProviderOrderId(generateProviderOrderId());
        transaction.setStatus(PaymentTransactionStatus.CREATED);
        transaction.setAmount(order.getPayableAmount());
        transaction.setCurrency(CURRENCY_INR);
        PaymentTransaction saved = paymentTransactionRepository.save(transaction);

        return toCreatePaymentOrderResponse(order, saved);
    }

    @Transactional
    public PaymentWebhookResponse reconcileWebhook(PaymentWebhookRequest request, String signature) {
        verifyWebhookSignature(request, signature);

        if (paymentWebhookEventRepository.findByProviderEventId(request.eventId()).isPresent()) {
            return new PaymentWebhookResponse(request.eventId(), request.providerOrderId(), "IGNORED_DUPLICATE");
        }

        PaymentTransaction transaction = paymentTransactionRepository.findByProviderOrderId(request.providerOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment transaction not found"));

        Order order = transaction.getOrder();
        String normalizedStatus = request.status().trim().toLowerCase(Locale.ROOT);

        if ("paid".equals(normalizedStatus) || "captured".equals(normalizedStatus)) {
            transaction.setStatus(PaymentTransactionStatus.CAPTURED);
            transaction.setProviderPaymentId(request.providerPaymentId());
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.CONFIRMED);
            inventoryService.consumeReservationsForOrder(order.getId());
        } else if ("failed".equals(normalizedStatus)) {
            transaction.setStatus(PaymentTransactionStatus.FAILED);
            transaction.setProviderPaymentId(request.providerPaymentId());
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
            inventoryService.releaseReservationsForOrder(order.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment webhook status");
        }

        paymentTransactionRepository.save(transaction);
        orderRepository.save(order);

        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.setProvider(PaymentProvider.RAZORPAY.name());
        event.setProviderEventId(request.eventId());
        event.setProviderOrderId(request.providerOrderId());
        event.setStatus(request.status());
        paymentWebhookEventRepository.save(event);

        return new PaymentWebhookResponse(request.eventId(), request.providerOrderId(), "PROCESSED");
    }

    private CreatePaymentOrderResponse toCreatePaymentOrderResponse(Order order, PaymentTransaction transaction) {
        return new CreatePaymentOrderResponse(
                order.getId(),
                transaction.getProvider().name(),
                transaction.getProviderOrderId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus().name()
        );
    }

    private String generateProviderOrderId() {
        long randomPart = 100000000000L + Math.abs(secureRandom.nextLong() % 900000000000L);
        return "order_" + randomPart;
    }

    private void verifyWebhookSignature(PaymentWebhookRequest request, String signature) {
        if (signature == null || signature.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Webhook signature missing");
        }

        String payload = request.eventId() + ":" + request.providerOrderId() + ":" + request.status();
        String expected = hmacSha256(payload, webhookSignatureSecret);
        if (!expected.equalsIgnoreCase(signature.trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid webhook signature");
        }
    }

    private String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Webhook signature verification failed");
        }
    }
}

