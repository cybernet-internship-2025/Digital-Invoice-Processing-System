package az.cybernet.invoice.notification.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceNotificationEvent {
    Long invoiceId;
    String senderTaxId;
    String receiverTaxId;
    String invoiceNumber;
    String invoiceSeries;
    String invoiceStatus;
    String previousStatus;
    BigDecimal totalPrice;
    LocalDateTime operationAt;
    String operationType;
    String comment;
}
