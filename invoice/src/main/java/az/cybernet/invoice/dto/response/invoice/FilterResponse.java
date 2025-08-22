package az.cybernet.invoice.dto.response.invoice;

import az.cybernet.invoice.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FilterResponse {
    Long id;
    String fullInvoiceNumber;
    String senderTaxId;
    String recipientTaxId;
    BigDecimal totalPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    InvoiceStatus status;

}
