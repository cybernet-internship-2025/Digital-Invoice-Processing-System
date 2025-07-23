package az.cybernet.invoice.dto.response.invoice;

import az.cybernet.invoice.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
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
@FieldDefaults(level = PRIVATE)
public class InvoiceResponse {
    Long id;
    Long senderTaxId;
    Long recipientTaxId;
    BigDecimal totalPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    InvoiceStatus status;
    String invoiceNumber;
    String invoiceSeries;
    List<ItemReponse> items;
}
