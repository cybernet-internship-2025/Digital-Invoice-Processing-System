package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.enums.ItemStatus;
import az.cybernet.invoice.exception.ExceptionConstants;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.ItemMapStruct;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.repository.ItemRepository;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.service.abstraction.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class ItemServiceImpl implements ItemService {
    private final ItemMapStruct itemMapStruct;
    private final ItemRepository itemRepository;
    private final InvoiceRepository invoiceRepository;
    private final MeasurementRepository measurementRepository;
    private final InvoiceService invoiceService;

    @Transactional
    @Override
    public List<ItemResponse> addItems(ItemsRequest itemsRequest) {
        if (itemsRequest == null || itemsRequest.getItemsRequest() == null || itemsRequest.getItemsRequest().isEmpty()) {
            return Collections.emptyList();
        }
        for (ItemRequest itemRequest : itemsRequest.getItemsRequest()) {
            MeasurementEntity measurement = measurementRepository.findByName(itemRequest.getMeasurementName());
            if (measurement == null) {
                throw new IllegalArgumentException("Measurement with name '" + itemRequest.getMeasurementName() + "' not found");
            }
        }
        itemRepository.addItems(itemsRequest);
//      operationRepository.addItems(itemsRequest);
        return findAllItemsByInvoiceId(itemsRequest.getInvoiceId());
    }

    @Transactional
    @Override
    public void updateItems(List<UpdateItemRequest> itemRequests) {
        for (UpdateItemRequest request : itemRequests) {
            ItemEntity item = itemRepository.findById(request.getId()).orElseThrow();

            MeasurementEntity measurement = measurementRepository.findByName(request.getMeasurementName());
            if (measurement == null) {
                throw new NotFoundException(ExceptionConstants.MEASUREMENT_NOT_FOUND.getMessage(),
                        ExceptionConstants.MEASUREMENT_NOT_FOUND.getCode());
            }

            item.setTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            item.setUpdatedAt(LocalDateTime.now());
            item.setStatus(ItemStatus.UPDATED);
            itemRepository.updateItems(item);
        }
    }

    @Override
    public List<ItemResponse> findAllItemsByInvoiceId(Long invoiceId) {
        invoiceService.fetchInvoiceIfExists(invoiceId);

        return itemRepository.findAllItemsByInvoiceId(invoiceId).stream()
                .map(itemMapStruct::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponse findById(Long id) {

        ItemEntity item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        return itemMapStruct.toResponse(item);
    }

    @Override
    public void deleteItemsByInvoiceId(Long invoiceId) {
        itemRepository.deleteItemsByInvoiceId(invoiceId);
    }

    @Override
    public void deleteItemsByItemsId(List<Long> ids) {
        itemRepository.deleteItemsByItemsId(ids);
    }
}
