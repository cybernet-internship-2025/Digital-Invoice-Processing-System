package az.cybernet.invoice.dto.request.create;

import lombok.Data;

import java.util.List;

@Data
public class ItemsRequest {
    Long invoiceId;
    List<ItemRequest> itemsRequest;
}
