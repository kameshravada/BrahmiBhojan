package com.brahmibhojan.modules.notifications.service;

import com.brahmibhojan.modules.notifications.gateway.EmailGateway;
import com.brahmibhojan.modules.notifications.gateway.SmsGateway;
import com.brahmibhojan.modules.notifications.gateway.WhatsAppGateway;
import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.model.NotificationEvent;
import com.brahmibhojan.modules.notifications.model.NotificationStatus;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import com.brahmibhojan.modules.notifications.repository.NotificationEventRepository;
import com.brahmibhojan.modules.orders.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationEventRepository notificationEventRepository;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationPreferenceService notificationPreferenceService;
    private final SmsGateway smsGateway;
    private final WhatsAppGateway whatsAppGateway;
    private final EmailGateway emailGateway;

    @Value("${notification.retry.max-attempts:5}")
    private int maxAttempts;

    @Value("${notification.retry.initial-delay-seconds:60}")
    private long initialRetryDelaySeconds;

    @Transactional
    public void sendOrderConfirmation(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_CONFIRMATION, false, context);
    }

    @Transactional
    public void sendOrderPaymentSuccess(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_PAYMENT_SUCCESS, false, context);
    }

    @Transactional
    public void sendOrderPaymentFailed(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_PAYMENT_FAILED, false, context);
    }

    @Transactional
    public void sendOrderPacked(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_PACKED, false, context);
    }

    @Transactional
    public void sendOrderShipped(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_SHIPPED, false, context);
    }

    @Transactional
    public void sendOrderOutForDelivery(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_OUT_FOR_DELIVERY, false, context);
    }

    @Transactional
    public void sendOrderDelivered(Order order) {
        Map<String, String> context = baseOrderContext(order);
        queueNotification(order.getUser().getId(), order.getPhoneNumber(), order.getUser().getEmail(),
                NotificationType.ORDER_DELIVERED, false, context);
    }

    @Transactional
    public void queueNotification(
            UUID userId,
            String mobile,
            String email,
            NotificationType type,
            boolean marketing,
            Map<String, String> context
    ) {
        Set<NotificationChannel> channels = resolveChannels(type, marketing, userId, email);
        for (NotificationChannel channel : channels) {
            NotificationTemplateService.RenderedTemplate rendered = notificationTemplateService.render(type, channel, context);

            NotificationEvent event = new NotificationEvent();
            event.setUserId(userId);
            event.setType(type);
            event.setChannel(channel);
            event.setRecipientMobile(mobile);
            event.setRecipientEmail(email);
            event.setSubject(rendered.subject());
            event.setMessage(rendered.body());
            event.setMarketing(marketing);
            event.setRetryCount(0);
            event.setMaxRetries(maxAttempts);
            event.setNextRetryAt(Instant.now());
            event.setIdempotencyKey(buildIdempotencyKey(type, channel, userId, mobile, email, context));

            if (notificationEventRepository.findByIdempotencyKey(event.getIdempotencyKey()).isPresent()) {
                continue;
            }

            NotificationEvent saved = notificationEventRepository.save(event);
            attemptDelivery(saved);
        }
    }

    @Transactional
    public int processPendingRetries() {
        Instant now = Instant.now();
        List<NotificationEvent> retryable = notificationEventRepository.findAllByStatusInAndNextRetryAtBefore(
                List.of(NotificationStatus.PENDING, NotificationStatus.FAILED),
                now
        );

        int processed = 0;
        for (NotificationEvent event : retryable) {
            if (event.getRetryCount() >= event.getMaxRetries() && event.getStatus() == NotificationStatus.FAILED) {
                continue;
            }
            attemptDelivery(event);
            processed++;
        }
        return processed;
    }

    private void attemptDelivery(NotificationEvent event) {
        try {
            String providerMessageId = switch (event.getChannel()) {
                case SMS -> smsGateway.sendSms(event.getRecipientMobile(), event.getMessage());
                case WHATSAPP -> whatsAppGateway.sendWhatsApp(event.getRecipientMobile(), event.getMessage());
                case EMAIL -> emailGateway.sendEmail(event.getRecipientEmail(), event.getSubject(), event.getMessage());
            };

            event.setStatus(NotificationStatus.SENT);
            event.setProviderMessageId(providerMessageId);
            event.setErrorMessage(null);
            event.setNextRetryAt(null);
        } catch (Exception ex) {
            int nextRetryCount = event.getRetryCount() + 1;
            event.setStatus(NotificationStatus.FAILED);
            event.setRetryCount(nextRetryCount);
            event.setErrorMessage(truncateError(ex.getMessage()));

            if (nextRetryCount <= event.getMaxRetries()) {
                long backoffSeconds = initialRetryDelaySeconds * (1L << Math.min(nextRetryCount - 1, 6));
                event.setNextRetryAt(Instant.now().plusSeconds(backoffSeconds));
            } else {
                event.setNextRetryAt(null);
            }
        }

        event.setLastAttemptAt(Instant.now());
        notificationEventRepository.save(event);
    }

    private Set<NotificationChannel> resolveChannels(NotificationType type, boolean marketing, UUID userId, String email) {
        if (type == NotificationType.OTP) {
            return EnumSet.of(NotificationChannel.SMS, NotificationChannel.WHATSAPP);
        }

        if (!marketing) {
            Set<NotificationChannel> channels = EnumSet.of(NotificationChannel.SMS, NotificationChannel.WHATSAPP);
            if (email != null && !email.isBlank()) {
                channels.add(NotificationChannel.EMAIL);
            }
            return channels;
        }

        Set<NotificationChannel> channels = EnumSet.noneOf(NotificationChannel.class);
        for (NotificationChannel channel : NotificationChannel.values()) {
            if (channel == NotificationChannel.EMAIL && (email == null || email.isBlank())) {
                continue;
            }
            if (notificationPreferenceService.isMarketingAllowed(userId, channel)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    private Map<String, String> baseOrderContext(Order order) {
        Map<String, String> context = new HashMap<>();
        context.put("name", order.getRecipientName());
        context.put("orderNumber", order.getOrderNumber());
        context.put("amount", order.getPayableAmount().toPlainString());
        return context;
    }

    private String buildIdempotencyKey(
            NotificationType type,
            NotificationChannel channel,
            UUID userId,
            String mobile,
            String email,
            Map<String, String> context
    ) {
        String userPart = userId == null ? "anonymous" : userId.toString();
        String recipientPart = mobile != null && !mobile.isBlank() ? mobile : String.valueOf(email);
        String orderPart = context == null ? null : context.get("orderNumber");
        if (orderPart == null || orderPart.isBlank()) {
            orderPart = UUID.randomUUID().toString();
        }
        return type + ":" + channel + ":" + userPart + ":" + recipientPart + ":" + orderPart;
    }

    private String truncateError(String value) {
        int maxLength = 500;
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}

