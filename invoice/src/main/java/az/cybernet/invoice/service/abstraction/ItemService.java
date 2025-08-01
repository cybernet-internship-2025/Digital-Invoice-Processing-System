package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;

import java.util.List;

public interface ItemService {
    void updateItem(List<UpdateItemRequest> itemRequests);

    List<ItemResponse> findAllItemsByInvoiceId(Long invoiceId);

    List<ItemResponse> addItems(ItemsRequest request);

    ItemResponse findById(Long id);

    void deleteItem(List<Long> ids);

    void restoreItem(Long itemId);
}