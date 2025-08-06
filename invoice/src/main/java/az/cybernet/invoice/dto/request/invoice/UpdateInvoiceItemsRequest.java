package az.cybernet.invoice.dto.request.invoice;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateInvoiceItemsRequest {
    String senderTaxId;
    Long invoiceId;
    List<Long>deletedItemsId;
    List<UpdateItemRequest>updatedItems;
    ItemsRequest createdItems;
}
