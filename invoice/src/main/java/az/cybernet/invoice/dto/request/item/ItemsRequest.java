package az.cybernet.invoice.dto.request.item;

import lombok.Data;

import java.util.List;

@Data
public class ItemsRequest {
    Long invoiceId;
    List<ItemRequest> itemsRequest;
}
