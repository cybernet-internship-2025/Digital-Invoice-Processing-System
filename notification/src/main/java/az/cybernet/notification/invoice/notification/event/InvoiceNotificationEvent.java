package az.cybernet.notification.invoice.notification.event;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceNotificationEvent {
    Long invoiceId;
    String senderTaxId;
    String receiverTaxId;
    String operationType;
}
