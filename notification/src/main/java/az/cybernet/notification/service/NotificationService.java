package az.cybernet.notification.service;


import az.cybernet.notification.invoice.notification.event.InvoiceNotificationEvent;

public interface NotificationService {
    void createNotification(InvoiceNotificationEvent event);
}
