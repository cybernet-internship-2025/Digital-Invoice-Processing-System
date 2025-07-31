package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.service.abstraction.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    @Override
    public void updateItem() {
    }

    @Override
    public void findAllItemsByInvoiceId() {    
    }
  
    @Override
    public void restoreItem() { 
    }

    @Override
    public void addItem(ItemRequest request) {   
    }

    @Override
    public ItemResponse findById(Long id) {
        return null;
    }

    @Override
    public void deleteItem(List<Long> ids) {
    }
}
