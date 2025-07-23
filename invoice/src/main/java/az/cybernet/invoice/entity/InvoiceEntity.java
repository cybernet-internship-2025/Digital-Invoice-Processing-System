package az.cybernet.invoice.entity;

import az.cybernet.invoice.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    InvoiceStatus status;
    String invoiceNumber;
    String invoiceSeries;
    List<OperationEntity> operations;
    List<ItemEntity> items;
}
