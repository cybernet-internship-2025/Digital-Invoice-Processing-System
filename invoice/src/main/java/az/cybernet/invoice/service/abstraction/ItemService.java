package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;

import java.util.List;

public interface ItemService {
    List<ItemResponse> addItems(ItemsRequest requests);

    void updateItems(List<UpdateItemRequest> itemRequests);

    List<ItemResponse> findAllItemsByInvoiceId(Long invoiceId);

    ItemResponse findById(Long id);

    void deleteItemsByInvoiceId(Long invoiceId);

    void deleteItemsByItemsId(List<Long> ids);
}