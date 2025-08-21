package az.cybernet.invoice.dto.response.invoice;



import lombok.Data;

import java.time.LocalDateTime;
@Data
public class FilterResponse {

    String fullInvoiceNumber;
    String senderId;
    String customerId;
    String status;
    Double total;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
