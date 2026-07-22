package com.brahmibhojan.modules.delivery.service;

import com.brahmibhojan.modules.delivery.dto.DeliveryWebhookRequest;
import com.brahmibhojan.modules.delivery.dto.DeliveryWebhookResponse;
import com.brahmibhojan.modules.delivery.model.DeliveryWebhookEvent;
import com.brahmibhojan.modules.delivery.repository.DeliveryWebhookEventRepository;
import com.brahmibhojan.modules.notifications.service.NotificationService;
import com.brahmibhojan.modules.orders.model.Order;
import com.brahmibhojan.modules.orders.model.OrderStatus;
import com.brahmibhojan.modules.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryTrackingService {

    private final OrderRepository orderRepository;
    private final DeliveryWebhookEventRepository deliveryWebhookEventRepository;
    private final NotificationService notificationService;
    private final DelhiveryStatusMapper delhiveryStatusMapper;
    private final GenericDeliveryStatusMapper genericDeliveryStatusMapper;

    @Value("${delivery.webhook.signature-secret:test-delivery-signature-secret}")
    private String deliveryWebhookSignatureSecret;

    @Transactional
    public DeliveryWebhookResponse reconcileWebhook(DeliveryWebhookRequest request, String signature) {
        verifyWebhookSignature(request, signature);

        String partner = sanitizePartner(request.partner());

        String externalEventId = buildExternalEventId(partner, request.eventId());
        if (deliveryWebhookEventRepository.findByExternalEventId(externalEventId).isPresent()) {
            return new DeliveryWebhookResponse(request.eventId(), request.orderNumber(), "IGNORED_DUPLICATE", null);
        }

        Order order = orderRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return new DeliveryWebhookResponse(request.eventId(), request.orderNumber(), "IGNORED_CANCELLED_ORDER", null);
        }

        OrderStatus incomingStatus = normalizeStatus(partner, request.status());
        if (rank(incomingStatus) <= rank(order.getStatus())) {
            return new DeliveryWebhookResponse(request.eventId(), request.orderNumber(), "IGNORED_STALE", incomingStatus.name());
        }

        order.setStatus(incomingStatus);
        orderRepository.save(order);
        sendStatusNotification(order, incomingStatus);

        DeliveryWebhookEvent event = new DeliveryWebhookEvent();
        event.setExternalEventId(externalEventId);
        event.setPartner(partner);
        event.setOrderNumber(request.orderNumber());
        event.setTrackingId(request.trackingId());
        event.setProviderStatus(request.status());
        event.setNormalizedStatus(incomingStatus.name());
        deliveryWebhookEventRepository.save(event);

        return new DeliveryWebhookResponse(request.eventId(), request.orderNumber(), "PROCESSED", incomingStatus.name());
    }

    private OrderStatus normalizeStatus(String partner, String providerStatus) {
        if (delhiveryStatusMapper.supports(partner)) {
            return delhiveryStatusMapper.normalize(providerStatus);
        }
        return genericDeliveryStatusMapper.normalize(providerStatus);
    }

    private int rank(OrderStatus status) {
        return switch (status) {
            case CREATED -> 0;
            case CONFIRMED -> 1;
            case PACKED -> 2;
            case SHIPPED -> 3;
            case OUT_FOR_DELIVERY -> 4;
            case DELIVERED -> 5;
            case CANCELLED -> 99;
        };
    }

    private void sendStatusNotification(Order order, OrderStatus status) {
        try {
            switch (status) {
                case PACKED -> notificationService.sendOrderPacked(order);
                case SHIPPED -> notificationService.sendOrderShipped(order);
                case OUT_FOR_DELIVERY -> notificationService.sendOrderOutForDelivery(order);
                case DELIVERED -> notificationService.sendOrderDelivered(order);
                default -> {
                    // no-op for non-delivery statuses
                }
            }
        } catch (Exception ex) {
            log.warn("Delivery status notification failed orderId={} status={}", order.getId(), status, ex);
        }
    }

    private void verifyWebhookSignature(DeliveryWebhookRequest request, String signature) {
        if (signature == null || signature.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Delivery webhook signature missing");
        }

        String payload = request.partner() + ":" + request.eventId() + ":" + request.orderNumber() + ":" + request.status();
        String expected = hmacSha256(payload, deliveryWebhookSignatureSecret);
        if (!expected.equalsIgnoreCase(signature.trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid delivery webhook signature");
        }
    }

    private String sanitizePartner(String partner) {
        return partner.trim().toUpperCase(Locale.ROOT);
    }

    private String buildExternalEventId(String partner, String eventId) {
        return partner.trim().toUpperCase(Locale.ROOT) + ":" + eventId.trim();
    }

    private String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delivery signature verification failed");
        }
    }
}

