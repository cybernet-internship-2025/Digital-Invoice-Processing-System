package az.cybernet.invoice.dto.request.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInvoiceRequest {
    String recipientTaxId;
}
