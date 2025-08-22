package az.cybernet.invoice.dto.request.invoice;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class UpdateInvoiceItemsRequest {
    String comment;
    String senderTaxId;
    Long invoiceId;
    List<Long> deletedItemsId;
    List<UpdateItemRequest> updatedItems;
    ItemsRequest createdItems;
}
