package az.cybernet.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class InvoiceEntity {
    Long id;
    Long senderUserId;
    Long recipientUserId;
    BigDecimal totalPrice;
    Timestamp createdAt;
    Timestamp updatedAt;
    String status;
    String invoiceNumber;
    String invoiceSeries;
}
