package az.cybernet.invoice.dto.request.invoice;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInvoiceRecipientTaxIdRequest {
    String recipientTaxId;
    Long invoiceId;
}
