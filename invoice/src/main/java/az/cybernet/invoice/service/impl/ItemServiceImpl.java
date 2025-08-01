package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.mapper.ItemMapStruct;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.repository.ItemRepository;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.abstraction.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    InvoiceRepository invoiceRepository;
    MeasurementRepository measurementRepository;
    ItemMapStruct itemMapStruct;

    @Override
    public void updateItem(List<UpdateItemRequest> itemRequests) {
        for (UpdateItemRequest request : itemRequests) {
            ItemEntity item = itemRepository.findById(request.getId()).orElseThrow();

//            if (request.getMeasurementName() != null) {
//                MeasurementEntity measurement = measurementRepository.findByName(request.getMeasurementName());
//                if (measurement != null) {
//                    item.setMeasurement(measurement);
//                }
//            }

            if (request.getName() != null) item.setName(request.getName());
            if (request.getUnitPrice() != null) item.setUnitPrice(request.getUnitPrice());
            if (request.getQuantity() != null) item.setQuantity(request.getQuantity());

            item.setTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            item.setUpdatedAt(LocalDateTime.now());
            itemRepository.updateItem(item);
        }
    }

    @Override
    public List<ItemResponse> findAllItemsByInvoiceId(Long invoiceId) {
        return itemRepository.findAllItemsByInvoiceId(invoiceId).stream()
                .map(itemMapStruct::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void restoreItem(Long itemId) {
        ItemEntity item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        item.setIsActive(true);
        item.setUpdatedAt(null);
        itemRepository.updateItem(item);
    }

    @Override
    public List<ItemResponse> addItems(ItemsRequest request) {
        return null;
    }

    @Override
    public ItemResponse findById(Long id) {
        return null;
    }

    @Override
    public void deleteItem(List<Long> ids) {
    }
}