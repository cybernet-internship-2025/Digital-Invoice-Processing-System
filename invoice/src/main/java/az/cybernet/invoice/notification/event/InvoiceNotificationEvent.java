package az.cybernet.invoice.notification.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class InvoiceNotificationEvent {
    Long invoiceId;
    String senderTaxId;
    String receiverTaxId;
    String operationType;
}
