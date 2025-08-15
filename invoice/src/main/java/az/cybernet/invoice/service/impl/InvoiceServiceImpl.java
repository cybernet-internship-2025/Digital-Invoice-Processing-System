package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.request.invoice.PaginatedInvoiceResponse;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.ReturnInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceToCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceItemsRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ReturnItemRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.enums.InvoiceStatus;
import az.cybernet.invoice.enums.ItemStatus;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.exception.InvalidStatusException;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.exception.UnauthorizedException;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.service.abstraction.ItemService;
import az.cybernet.invoice.service.abstraction.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static az.cybernet.invoice.enums.InvoiceStatus.APPROVED;
import static az.cybernet.invoice.enums.InvoiceStatus.CORRECTION;
import static az.cybernet.invoice.enums.InvoiceStatus.PENDING;
import static az.cybernet.invoice.enums.OperationStatus.DRAFT;
import static az.cybernet.invoice.enums.OperationStatus.UPDATE;
import static az.cybernet.invoice.exception.ExceptionConstants.INVALID_STATUS;
import static az.cybernet.invoice.exception.ExceptionConstants.INVOICE_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.ITEM_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.SENDER_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.UNAUTHORIZED;
import static az.cybernet.invoice.util.GeneralUtil.isNullOrEmpty;
import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    UserClient userClient;
    InvoiceMapper invoiceMapper;
    ItemService itemService;
    OperationService operationService;
    static int MAX_SIZE = 50;
    static int MIN_SIZE = 10;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest) {
        findSenderByTaxId(invoiceRequest.getSenderTaxId());
        findRecipientByTaxId(invoiceRequest.getRecipientTaxId());

        var invoiceEntity = invoiceMapper.fromInvoiceRequestToEntity(invoiceRequest);
        invoiceEntity.setInvoiceNumber(generateInvoiceNumber());
        invoiceEntity.setInvoiceSeries("INVD");
        invoiceEntity.setTotalPrice(ZERO);

        invoiceRepository.saveInvoice(invoiceEntity);
        Long invoiceId = invoiceEntity.getId();

        if (invoiceRequest.getItems() != null && invoiceRequest.getItems().getItemsRequest() != null &&
                !invoiceRequest.getItems().getItemsRequest().isEmpty()) {

            var items = invoiceRequest.getItems();
            items.setInvoiceId(invoiceId);

            for (ItemRequest i : items.getItemsRequest()) {
                validateItem(i);
            }

            itemService.addItems(items);

            var totalPrice = updateInvoiceTotalPrice(invoiceId);
            invoiceEntity.setTotalPrice(totalPrice);
        }

        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    private void validateItem(ItemRequest i) {
        if (i.getQuantity() == null || i.getQuantity() <= 0) {
            throw new IllegalArgumentException("Item quantity must be > 0");
        }
        if (i.getUnitPrice() == null || i.getUnitPrice().compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Item unitPrice must be >= 0");
        }
    }

    private BigDecimal updateInvoiceTotalPrice(Long invoiceId) {
        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);

        if (items == null) {
            items = Collections.emptyList();
        }

        BigDecimal total = items.stream()
                .map(ItemResponse::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoiceRepository.updateTotalPrice(invoiceId, total);
        return total;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approveInvoice(ApproveAndCancelInvoiceRequest request) {
        if (request.getInvoiceIds() == null || request.getInvoiceIds().isEmpty()) {
            throw new IllegalArgumentException("No invoice IDs provided");
        }

        for (Long invoiceId : request.getInvoiceIds()) {
            var invoiceEntity = fetchInvoiceIfExist(invoiceId);

            if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId())
                    || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
                throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
            }

            if (invoiceEntity.getStatus() != PENDING) {
                throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
            }

            invoiceRepository.updateInvoiceStatus(invoiceId, InvoiceStatus.APPROVED, LocalDateTime.now());

            var itemResponses = itemService.findAllItemsByInvoiceId(invoiceId);

            for (var item : itemResponses) {
                itemService.updateItemStatus(item.getId(), ItemStatus.CREATED);
            }

            addInvoiceToOperation(invoiceId, "Invoice approved", OperationStatus.APPROVED);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelInvoice(ApproveAndCancelInvoiceRequest request) {
        if (request.getInvoiceIds() == null || request.getInvoiceIds().isEmpty()) {
            throw new IllegalArgumentException("No invoice IDs provided");
        }

        for (Long invoiceId : request.getInvoiceIds()) {
            var invoiceEntity = fetchInvoiceIfExist(invoiceId);

            if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId())
                    || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
                throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
            }

            if (invoiceEntity.getStatus() != PENDING) {
                throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
            }

            invoiceRepository.updateInvoiceStatus(invoiceId, InvoiceStatus.CANCELED, LocalDateTime.now());

            var itemResponses = itemService.findAllItemsByInvoiceId(invoiceId);

            for (var item : itemResponses) {
                itemService.updateItemStatus(item.getId(), ItemStatus.DELETED);
            }

            addInvoiceToOperation(invoiceId, "Invoice approved", OperationStatus.APPROVED);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void requestCorrection(Long invoiceId, RequestCorrectionRequest request) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId()) || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING || invoiceEntity.getStatus() != APPROVED) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceRepository.updateInvoiceStatus(invoiceEntity.getId(), InvoiceStatus.CORRECTION, LocalDateTime.now());

        String opComment = (request.getComment() == null || request.getComment().isBlank())
                ? "Correction requested"
                : request.getComment();

        var itemResponses = itemService.findAllItemsByInvoiceId(invoiceId);

        for (var item : itemResponses) {
            itemService.updateItemStatus(item.getId(), ItemStatus.UPDATED);
        }

        itemService.addItemsToOperation(invoiceEntity.getId(), opComment, OperationStatus.CORRECTION);
    }

    private void addInvoiceToOperation(Long invoiceId, String comment, OperationStatus status) {
        InvoiceEntity invoiceEntity = fetchInvoiceIfExist(invoiceId);

        CreateOperationRequest operationRequest = CreateOperationRequest.builder()
                .status(status)
                .invoiceId(invoiceEntity.getId())
                .comment(comment)
                .build();

        operationService.saveOperation(operationRequest);

    }

    @Override
    public InvoiceResponse findById(Long id) {
        var invoiceEntity = fetchInvoiceIfExist(id);
        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    @Override
    public PaginatedInvoiceResponse findAllByRecipientUserTaxId(String recipientTaxId,
                                                                InvoiceFilterRequest filter,
                                                                Integer page,
                                                                Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = MIN_SIZE;
        } else if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        filter.setOffset(page * size);
        filter.setLimit(size);

        var userResponse = findRecipientByTaxId(recipientTaxId);
        var allByRecipientUserTaxId = invoiceRepository
                .findAllInvoicesByRecipientUserTaxId(userResponse.getTaxId(), filter);

        var count = invoiceRepository
                .countInvoicesByRecipientUserTaxId(userResponse.getTaxId(), filter);
        boolean hasNext = count > (long) (page + 1) * size;

        List<InvoiceResponse> invoiceResponses = invoiceMapper
                .allByRecipientUserTaxId(allByRecipientUserTaxId);

        return PaginatedInvoiceResponse.builder()
                .invoices(invoiceResponses)
                .hasNext(hasNext)
                .build();
    }

    private UserResponse findSenderByTaxId(String senderTaxId) {
        UserResponse sender = userClient.findUserByTaxId(senderTaxId);

        if (sender == null) {
            throw new NotFoundException(SENDER_NOT_FOUND.getCode(), SENDER_NOT_FOUND.getMessage());
        }

        return sender;
    }

    private UserResponse findRecipientByTaxId(String recipientTaxId) {
        UserResponse recipient = userClient.findUserByTaxId(recipientTaxId);

        if (recipient == null) {
            throw new NotFoundException(RECIPIENT_NOT_FOUND.getCode(), RECIPIENT_NOT_FOUND.getMessage());
        }

        return recipient;
    }

    private String generateInvoiceNumber() {
        var now = LocalDateTime.now();
        var year = String.valueOf(now.getYear()).substring(2);
        var month = String.format("%02d", now.getMonthValue());
        var prefix = year + month;

        Long nextVal = invoiceRepository.getNextInvoiceSequence();
        var sequence = String.format("%04d", nextVal);

        return prefix + sequence;
    }

    @Override
    public InvoiceEntity fetchInvoiceIfExist(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));
    }

    @Override
    @Transactional
    public void deleteInvoiceById(Long invoiceId) {
        fetchInvoiceIfExist(invoiceId);
        //TODO: Check invoice status
        invoiceRepository.deleteInvoiceById(invoiceId);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId) {
        //TODO: check current user has invoice given by ID or not
        InvoiceEntity invoice = fetchInvoiceIfExist(invoiceId);

        if (doesntMatchInvoiceStatus(invoice, CORRECTION, InvoiceStatus.DRAFT)) {
            throw new RuntimeException("You cannot update invoice if it isn't on DRAFT or CORRECTION status!");
        }

//        findRecipientByTaxId(recipientTaxId);

        invoice.setRecipientTaxId(recipientTaxId);

        invoiceRepository.updateInvoiceRecipientTaxId(invoiceId, recipientTaxId);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoice(SendInvoiceRequest request) {
        InvoiceEntity invoice = invoiceRepository.findBySenderTaxIdAndInvoiceId(request.getSenderUserTaxId(), request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("You have no any invoice given by ID!"));

        if (doesntMatchInvoiceStatus(invoice, CORRECTION, InvoiceStatus.DRAFT)) {
            throw new RuntimeException("Your invoice isn't on DRAFT or CORRECTION status!");
        }

        invoiceRepository.changeStatus(request.getInvoiceId(), PENDING.toString());
        invoice.setStatus(PENDING);

        List<Long> itemsId = invoice.getItems().stream().map(ItemEntity::getId).toList();
        addInvoiceToOperation(request.getInvoiceId(), "Invoice sent", OperationStatus.PENDING, itemsId);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoiceToCorrection(SendInvoiceToCorrectionRequest request) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndReceiverTaxId(request.getInvoiceId(), request.getReceiverTaxId())
                .orElseThrow(() -> new RuntimeException("You have not any received active invoice and given by ID!"));

        if (doesntMatchInvoiceStatus(invoice, PENDING)) {
            throw new RuntimeException("You have not any received active invoice on status PENDING");
        }

        invoiceRepository.changeStatus(request.getInvoiceId(), CORRECTION.toString());

        invoice.setStatus(CORRECTION);

        addInvoiceToOperation(request.getInvoiceId(), request.getComment(), OperationStatus.CORRECTION, null);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse rollbackInvoice(Long invoiceId, String senderTaxId) {
        InvoiceEntity invoice = invoiceRepository.findBySenderTaxIdAndInvoiceId(senderTaxId, invoiceId)
                .orElseThrow(() -> new RuntimeException("You have not any invoice and given by ID"));

        if (doesntMatchInvoiceStatus(invoice, PENDING)) {
            throw new RuntimeException("Your invoice isn't on PENDING status!");
        }

        invoice.setStatus(InvoiceStatus.DRAFT);

        addInvoiceToOperation(invoiceId, "Invoice rolled back", DRAFT, null);

        invoiceRepository.changeStatus(invoiceId, InvoiceStatus.DRAFT.toString());

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    public PagedResponse<InvoiceResponse> findInvoicesBySenderTaxId(InvoiceFilterRequest filter) {
        int queryLimit = filter.getLimit() + 1;
        filter.setLimit(queryLimit);


        List<InvoiceEntity> entities = invoiceRepository.findInvoicesBySenderTaxId(filter);

        boolean hasNext = entities.size() > filter.getLimit() - 1;


        if (hasNext) {
            entities.removeLast(); // remove last item
        }

        PagedResponse<InvoiceResponse> response = new PagedResponse<>();
        response.setContent(invoiceMapper.allByRecipientUserTaxId(entities));
        response.setHasNext(hasNext);
        response.setOffset(filter.getOffset());
        response.setLimit(filter.getLimit() - 1);
        return response;

    }


    @Override
    public InvoiceResponse updateInvoiceItems(UpdateInvoiceItemsRequest request) {
        InvoiceEntity invoice = invoiceRepository.findBySenderTaxIdAndInvoiceId(request.getSenderTaxId(), request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("You have not any invoice given by ID"));

        if (doesntMatchInvoiceStatus(invoice, CORRECTION)) {
            throw new RuntimeException("Your invoice isn't on CORRECTION status!");
        }

        if (!isNullOrEmpty(request.getCreatedItems().getItemsRequest())) {
            itemService.addItems(request.getCreatedItems());
        }

        if (!isNullOrEmpty(request.getUpdatedItems())) {
            itemService.updateItems(request.getUpdatedItems());
        }

        if (!isNullOrEmpty(request.getDeletedItemsId())) {
            itemService.deleteItemsByItemsId(request.getDeletedItemsId());
        }

        //TODO: update invoice updatedAt field when one of items related with invoice updated

        invoiceRepository.refreshInvoice(request.getInvoiceId());
        invoice = fetchInvoiceIfExist(request.getInvoiceId());

        addInvoiceToOperation(request.getInvoiceId(), "Invoice updated", UPDATE, null);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceResponse createReturnInvoice(ReturnInvoiceRequest invoiceRequest, String currentUserTaxId) {
        var originalInvoice = fetchInvoiceIfExist(invoiceRequest.getOriginalInvoiceId());

        if (!originalInvoice.getRecipientTaxId().equals(currentUserTaxId)) {
            throw new IllegalStateException("Only the recipient of the original invoice can create a return invoice");
        }

        var invoiceEntity = InvoiceEntity.builder()
                .senderTaxId(originalInvoice.getRecipientTaxId())
                .recipientTaxId(originalInvoice.getSenderTaxId())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .status(InvoiceStatus.DRAFT)
                .invoiceSeries("INR")
                .invoiceNumber(generateInvoiceNumber())
                .isActive(true)
                .build();

        List<ItemEntity> returnedItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (ReturnItemRequest ri : invoiceRequest.getItems()) {
            ItemEntity originalItem = originalInvoice.getItems().stream()
                    .filter(i -> i.getId().equals(ri.getItemId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND.getCode(), ITEM_NOT_FOUND.getMessage()));

            if (ri.getQuantity().compareTo(originalItem.getQuantity()) > 0) {
                throw new IllegalArgumentException("Return quantity cannot be greater than original quantity");
            }

            ItemEntity returnedItem = ItemEntity.builder()
                    .name(originalItem.getName())
                    .unitPrice(originalItem.getUnitPrice())
                    .quantity(ri.getQuantity())
                    .isActive(true)
                    .totalPrice(originalItem.getUnitPrice().multiply(BigDecimal.valueOf(ri.getQuantity())))
                    .measurement(originalItem.getMeasurement())
                    .invoice(invoiceEntity)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(ItemStatus.CREATED)
                    .build();

            totalPrice = totalPrice.add(returnedItem.getTotalPrice());

            returnedItems.add(returnedItem);
        }

        invoiceEntity.setItems(returnedItems);
        invoiceEntity.setTotalPrice(totalPrice);

        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    @Override
    public InvoiceResponse sendReturnInvoice(Long invoiceId, String senderTaxId, String recipientTaxId) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!"INR".equals(invoiceEntity.getInvoiceSeries())) {
            throw new IllegalStateException("This is not a return (INR) invoice");
        }

        if (!invoiceEntity.getRecipientTaxId().equals(senderTaxId)) {
            throw new IllegalStateException("Only the sender can send this return invoice");
        }

        if (!invoiceEntity.getSenderTaxId().equals(recipientTaxId)) {
            throw new IllegalStateException("Only the sender can send this return invoice");
        }

        if (!InvoiceStatus.DRAFT.equals(invoiceEntity.getStatus())) {
            throw new IllegalStateException("Only draft invoices can be sent");
        }

        invoiceRepository.updateInvoiceStatus(invoiceId, InvoiceStatus.PENDING, LocalDateTime.now());
        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    private boolean doesntMatchInvoiceStatus(InvoiceEntity invoice, InvoiceStatus... statuses) {
        return !Arrays.asList(statuses).contains(invoice.getStatus());
    }

}
