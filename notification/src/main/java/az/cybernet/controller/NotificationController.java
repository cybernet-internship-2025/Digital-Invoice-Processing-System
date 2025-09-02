package az.cybernet.controller;

import az.cybernet.invoice.notification.event.InvoiceNotificationEvent;
import az.cybernet.service.abstact.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    @PostMapping("/create")
    public void create(@RequestBody InvoiceNotificationEvent event){
        notificationService.createNotification(event);
    }
}
