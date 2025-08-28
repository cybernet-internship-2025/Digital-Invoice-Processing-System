package az.cybernet.service.impl;

import az.cybernet.invoice.notification.event.InvoiceNotificationEvent;
import az.cybernet.entity.NotificationEntity;
import az.cybernet.repository.NotificationRepository;
import az.cybernet.service.abstact.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;

    public void createNotification(InvoiceNotificationEvent event){
        String message=buildMessage(event.getOperationType());
        NotificationEntity notification=NotificationEntity.builder()
                .invoiceId(event.getInvoiceId())
                .senderTaxId(event.getSenderTaxId())
                .receiverTaxId(event.getReceiverTaxId())
                .operationType(event.getOperationType())
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    private String buildMessage(String operationType){
        return switch (operationType) {
            case "DRAFT", "PENDING" -> "A new invoice has been created and is pending.";
            case "APPROVED" -> "The invoice has been approved.";
            case "CANCELED" -> "The invoice has been canceled.";
            case "CORRECTION" -> "The invoice requires correction.";
            case "UPDATE" -> "The invoice has been updated.";
            case "DELETE" -> "The invoice has been deleted.";
            case "SEND_TO_CANCEL" -> "The invoice has been rejected.";
            default -> "Invoice status changed.";
        };
    }
}
