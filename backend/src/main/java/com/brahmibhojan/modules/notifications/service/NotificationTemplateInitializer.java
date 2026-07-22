package com.brahmibhojan.modules.notifications.service;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.model.NotificationTemplate;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import com.brahmibhojan.modules.notifications.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "notification.template", name = "seed-enabled", havingValue = "true", matchIfMissing = true)
public class NotificationTemplateInitializer implements CommandLineRunner {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public void run(String... args) {
        if (notificationTemplateRepository.count() > 0) {
            return;
        }

        seed(NotificationType.ORDER_CONFIRMATION, NotificationChannel.SMS,
                null,
                "Hi {{name}}, your order {{orderNumber}} is created for amount {{amount}}.");
        seed(NotificationType.ORDER_PAYMENT_SUCCESS, NotificationChannel.SMS,
                null,
                "Hi {{name}}, payment received for order {{orderNumber}}.");
        seed(NotificationType.ORDER_PAYMENT_FAILED, NotificationChannel.SMS,
                null,
                "Hi {{name}}, payment failed for order {{orderNumber}}. Please retry.");
        seed(NotificationType.ORDER_PACKED, NotificationChannel.SMS,
                null,
                "Hi {{name}}, your order {{orderNumber}} is packed and ready for dispatch.");
        seed(NotificationType.ORDER_SHIPPED, NotificationChannel.SMS,
                null,
                "Hi {{name}}, your order {{orderNumber}} has been shipped.");
        seed(NotificationType.ORDER_OUT_FOR_DELIVERY, NotificationChannel.SMS,
                null,
                "Hi {{name}}, your order {{orderNumber}} is out for delivery.");
        seed(NotificationType.ORDER_DELIVERED, NotificationChannel.SMS,
                null,
                "Hi {{name}}, your order {{orderNumber}} has been delivered. Enjoy your meal!");

        seed(NotificationType.ORDER_CONFIRMATION, NotificationChannel.EMAIL,
                "Order {{orderNumber}} created",
                "Hi {{name}}, your order {{orderNumber}} is created for amount {{amount}}.");
        seed(NotificationType.ORDER_PAYMENT_SUCCESS, NotificationChannel.EMAIL,
                "Payment successful for {{orderNumber}}",
                "Hi {{name}}, payment received for order {{orderNumber}}.");
        seed(NotificationType.ORDER_PAYMENT_FAILED, NotificationChannel.EMAIL,
                "Payment failed for {{orderNumber}}",
                "Hi {{name}}, payment failed for order {{orderNumber}}. Please retry.");
        seed(NotificationType.ORDER_PACKED, NotificationChannel.EMAIL,
                "Order {{orderNumber}} packed",
                "Hi {{name}}, your order {{orderNumber}} is packed and ready for dispatch.");
        seed(NotificationType.ORDER_SHIPPED, NotificationChannel.EMAIL,
                "Order {{orderNumber}} shipped",
                "Hi {{name}}, your order {{orderNumber}} has been shipped.");
        seed(NotificationType.ORDER_OUT_FOR_DELIVERY, NotificationChannel.EMAIL,
                "Order {{orderNumber}} out for delivery",
                "Hi {{name}}, your order {{orderNumber}} is out for delivery.");
        seed(NotificationType.ORDER_DELIVERED, NotificationChannel.EMAIL,
                "Order {{orderNumber}} delivered",
                "Hi {{name}}, your order {{orderNumber}} has been delivered. Enjoy your meal!");

        seed(NotificationType.ORDER_CONFIRMATION, NotificationChannel.WHATSAPP,
                null,
                "Order {{orderNumber}} created for amount {{amount}}.");
        seed(NotificationType.ORDER_PAYMENT_SUCCESS, NotificationChannel.WHATSAPP,
                null,
                "Payment successful for order {{orderNumber}}.");
        seed(NotificationType.ORDER_PAYMENT_FAILED, NotificationChannel.WHATSAPP,
                null,
                "Payment failed for order {{orderNumber}}. Please retry.");
        seed(NotificationType.ORDER_PACKED, NotificationChannel.WHATSAPP,
                null,
                "Your order {{orderNumber}} is packed and ready for dispatch.");
        seed(NotificationType.ORDER_SHIPPED, NotificationChannel.WHATSAPP,
                null,
                "Your order {{orderNumber}} has been shipped.");
        seed(NotificationType.ORDER_OUT_FOR_DELIVERY, NotificationChannel.WHATSAPP,
                null,
                "Your order {{orderNumber}} is out for delivery.");
        seed(NotificationType.ORDER_DELIVERED, NotificationChannel.WHATSAPP,
                null,
                "Your order {{orderNumber}} has been delivered. Enjoy your meal!");

        log.info("Notification templates seeded");
    }

    private void seed(NotificationType type, NotificationChannel channel, String subject, String body) {
        NotificationTemplate template = new NotificationTemplate();
        template.setType(type);
        template.setChannel(channel);
        template.setSubject(subject);
        template.setBody(body);
        template.setActive(true);
        notificationTemplateRepository.save(template);
    }
}

