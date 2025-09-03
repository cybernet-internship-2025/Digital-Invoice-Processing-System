package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.request.operation.AddItemsToOperationRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationDetailsRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.enums.ItemStatus;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.exception.ExceptionConstants;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.ItemMapStruct;
import az.cybernet.invoice.repository.*;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.service.abstraction.ItemService;
import az.cybernet.invoice.service.abstraction.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Log
public class ItemServiceImpl implements ItemService {
    private final ItemMapStruct itemMapStruct;
    private final ItemRepository itemRepository;
    private final OperationService operationService;
    private final InvoiceService invoiceService;
    private final MeasurementRepository measurementRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ItemResponse> addItems(ItemsRequest itemsRequest) {
        if (itemsRequest == null || itemsRequest.getItemsRequest() == null || itemsRequest.getItemsRequest().isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemEntity> itemsToSave = new ArrayList<>();

        for (ItemRequest itemRequest : itemsRequest.getItemsRequest()) {
            MeasurementEntity measurement = measurementRepository.findByName(itemRequest.getMeasurementName())
                    .orElseThrow(
                            () -> new NotFoundException(
                                    ExceptionConstants.MEASUREMENT_NOT_FOUND.getMessage(),
                                    ExceptionConstants.MEASUREMENT_NOT_FOUND.getCode()
                            ));

            ItemEntity item = ItemEntity.builder()
                    .status(ItemStatus.CREATED)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .name(itemRequest.getName())
                    .invoice(InvoiceEntity.builder().id(itemsRequest.getInvoiceId()).build())
                    .measurement(MeasurementEntity.builder().name(itemRequest.getMeasurementName()).build())
                    .unitPrice(itemRequest.getUnitPrice())
                    .quantity(itemRequest.getQuantity())
                    .totalPrice(itemRequest.getUnitPrice() != null && itemRequest.getQuantity() != null
                            ? itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                            : BigDecimal.ZERO)
                    .build();

            itemsToSave.add(item);
        }
        itemRepository.addItems(itemsToSave);

        List<ItemEntity> items = itemRepository.findAllItemsByInvoiceId(itemsRequest.getInvoiceId());
        List<Long> itemIds = items.stream()
                .map(ItemEntity::getId)
                .filter(Objects::nonNull)
                .toList();

        addItemsToOperation(
                AddItemsToOperationRequest.builder()
                        .invoiceId(itemsRequest.getInvoiceId())
                        .comment("Items added")
                        .status(OperationStatus.DRAFT)
                        .itemIds(itemIds)
                        .build()
        );

        return itemMapStruct.toResponseList(items);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addItemsToOperation(AddItemsToOperationRequest request) {
        Long invoiceId = request.getInvoiceId();
        List<Long> itemIds = request.getItemIds();

        if (itemIds == null) {
            itemIds = findAllItemsByInvoiceId(invoiceId).stream()
                    .map(ItemResponse::getId)
                    .filter(Objects::nonNull)
                    .toList();
        }

        List<CreateOperationDetailsRequest> itemsForOp = itemIds.stream()
                .map(itemId -> CreateOperationDetailsRequest.builder()
                        .itemId(itemId)
                        .itemStatus(request.getStatus() == OperationStatus.DRAFT
                                ? ItemStatus.CREATED
                                : ItemStatus.UPDATED)
                        .build())
                .toList();

        CreateOperationRequest opReq = CreateOperationRequest.builder()
                .invoiceId(invoiceId)
                .status(request.getStatus())
                .comment(request.getComment())
                .items(itemsForOp)
                .build();

        operationService.saveOperation(opReq);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateItemStatus(Long itemId, ItemStatus status) {
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        "Item not found with id: " + itemId,
                        ExceptionConstants.ITEM_NOT_FOUND.getCode()
                ));
        item.setStatus(status);
        item.setUpdatedAt(LocalDateTime.now());

        itemRepository.updateItems(item);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateItems(List<UpdateItemRequest> itemRequests, Long invoiceId) {
        for (UpdateItemRequest request : itemRequests) {
            ItemEntity item = itemRepository.findById(request.getId()).orElseThrow();

            MeasurementEntity measurement = measurementRepository.findByName(request.getMeasurementName())
                    .orElseThrow(
                            () -> new NotFoundException(
                                    ExceptionConstants.MEASUREMENT_NOT_FOUND.getMessage(),
                                    ExceptionConstants.MEASUREMENT_NOT_FOUND.getCode()
                            ));

            item.setTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            item.setUpdatedAt(LocalDateTime.now());
            item.setStatus(ItemStatus.UPDATED);
            itemRepository.updateItems(item);
        }

        List<ItemResponse> itemResponses = findAllItemsByInvoiceId(invoiceId);
        List<Long> itemIds = itemResponses.stream()
                .map(ItemResponse::getId)
                .filter(Objects::nonNull)
                .toList();

        addItemsToOperation(
                AddItemsToOperationRequest.builder()
                        .invoiceId(invoiceId)
                        .comment("Items updated")
                        .status(OperationStatus.UPDATE)
                        .itemIds(itemIds)
                        .build()
        );
    }

    @Override
    public List<ItemResponse> findAllItemsByInvoiceId(Long invoiceId) {
        invoiceService.fetchInvoiceIfExist(invoiceId);

        return itemRepository.findAllItemsByInvoiceId(invoiceId).stream()
                .map(itemMapStruct::toResponse)
                .collect(toList());
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
