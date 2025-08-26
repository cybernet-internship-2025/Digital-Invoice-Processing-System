package az.cybernet.invoice.dto.response.invoice;

import az.cybernet.invoice.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FilterResponse {
    private Long id;
    private String fullInvoiceNumber;
    private String senderTaxId;
    private String recipientTaxId;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private InvoiceStatus status;

}
