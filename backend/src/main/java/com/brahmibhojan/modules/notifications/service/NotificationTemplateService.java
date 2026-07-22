package com.brahmibhojan.modules.notifications.service;

import com.brahmibhojan.modules.notifications.model.NotificationChannel;
import com.brahmibhojan.modules.notifications.model.NotificationTemplate;
import com.brahmibhojan.modules.notifications.model.NotificationType;
import com.brahmibhojan.modules.notifications.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    public RenderedTemplate render(NotificationType type, NotificationChannel channel, Map<String, String> context) {
        NotificationTemplate template = notificationTemplateRepository
                .findByTypeAndChannelAndActiveTrue(type, channel)
                .orElseGet(() -> fallbackTemplate(type, channel));

        String subject = applyVariables(template.getSubject(), context);
        String body = applyVariables(template.getBody(), context);
        return new RenderedTemplate(subject, body);
    }

    private NotificationTemplate fallbackTemplate(NotificationType type, NotificationChannel channel) {
        NotificationTemplate template = new NotificationTemplate();
        template.setType(type);
        template.setChannel(channel);
        template.setActive(true);

        switch (type) {
            case ORDER_CONFIRMATION -> {
                template.setSubject("Order {{orderNumber}} created");
                template.setBody("Hi {{name}}, your order {{orderNumber}} is created for amount {{amount}}.");
            }
            case ORDER_PAYMENT_SUCCESS -> {
                template.setSubject("Payment successful for {{orderNumber}}");
                template.setBody("Hi {{name}}, payment received for order {{orderNumber}}.");
            }
            case ORDER_PAYMENT_FAILED -> {
                template.setSubject("Payment failed for {{orderNumber}}");
                template.setBody("Hi {{name}}, payment failed for order {{orderNumber}}. Please retry.");
            }
            case ORDER_PACKED -> {
                template.setSubject("Order {{orderNumber}} packed");
                template.setBody("Hi {{name}}, your order {{orderNumber}} is packed and ready for dispatch.");
            }
            case ORDER_SHIPPED -> {
                template.setSubject("Order {{orderNumber}} shipped");
                template.setBody("Hi {{name}}, your order {{orderNumber}} has been shipped.");
            }
            case ORDER_OUT_FOR_DELIVERY -> {
                template.setSubject("Order {{orderNumber}} out for delivery");
                template.setBody("Hi {{name}}, your order {{orderNumber}} is out for delivery.");
            }
            case ORDER_DELIVERED -> {
                template.setSubject("Order {{orderNumber}} delivered");
                template.setBody("Hi {{name}}, your order {{orderNumber}} has been delivered. Enjoy your meal!");
            }
            default -> {
                template.setSubject(type.name().replace('_', ' '));
                template.setBody("Notification for " + type.name().replace('_', ' ') + ".");
            }
        }
        return template;
    }

    private String applyVariables(String value, Map<String, String> context) {
        if (value == null || value.isBlank() || context == null || context.isEmpty()) {
            return value;
        }
        String rendered = value;
        for (Map.Entry<String, String> entry : context.entrySet()) {
            String key = "{{" + entry.getKey() + "}}";
            rendered = rendered.replace(key, entry.getValue() == null ? "" : entry.getValue());
        }
        return rendered;
    }

    public record RenderedTemplate(String subject, String body) {
    }
}

