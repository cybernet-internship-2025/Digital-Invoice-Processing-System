package az.cybernet.notification.notification.event;

import az.cybernet.notification.invoice.notification.event.InvoiceNotificationEvent;
import az.cybernet.notification.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InvoiceNotificationListener {
    NotificationService notificationService;

    @KafkaListener(topics = "invoice-notifications",groupId = "notification-group")
    public void listen(InvoiceNotificationEvent event){
        notificationService.createNotification(event);
        log.info("event-send");
    }
}
