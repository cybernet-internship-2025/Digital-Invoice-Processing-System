package az.cybernet.invoice.service.abstraction;


import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;

import java.util.List;

public interface ItemService {

    void addItem(ItemRequest request);

    ItemResponse findById(Long id);

    void deleteItem(List<Long> ids);



}
