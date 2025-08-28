package az.cybernet.service.abstact;

import az.cybernet.invoice.notification.event.InvoiceNotificationEvent;

public interface NotificationService {
    void createNotification(InvoiceNotificationEvent event);
}
