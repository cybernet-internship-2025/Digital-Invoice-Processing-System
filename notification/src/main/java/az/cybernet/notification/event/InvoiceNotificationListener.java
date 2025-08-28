package az.cybernet.notification.event;

import az.cybernet.invoice.notification.event.InvoiceNotificationEvent;
import az.cybernet.service.abstact.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class InvoiceNotificationListener {
    NotificationService notificationService;

    @KafkaListener(topics = "invoice-notifications",groupId = "notification-group")
    public void listen(InvoiceNotificationEvent event){
        notificationService.createNotification(event);
    }
}
