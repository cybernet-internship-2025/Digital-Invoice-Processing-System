package az.cybernet.invoice.dto.request.invoice;

import az.cybernet.invoice.dto.request.item.ReturnItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ReturnInvoiceRequest {
    Long originalInvoiceId;
    List<ReturnItemRequest> items;
    String senderTaxId;
    String recipientTaxId;
}
