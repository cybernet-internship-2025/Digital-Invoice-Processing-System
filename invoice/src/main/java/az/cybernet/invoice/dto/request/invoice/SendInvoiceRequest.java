package az.cybernet.invoice.dto.request.invoice;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendInvoiceRequest {
    String senderUserTaxId;
    List<Long> invoiceIds;
}
