package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceToCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceItemsRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationDetailsRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
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

        addInvoiceToOperation(invoiceId, "Invoice created", OperationStatus.DRAFT, null);

        if (invoiceRequest.getItems() != null && invoiceRequest.getItems().getItemsRequest() != null &&
                !invoiceRequest.getItems().getItemsRequest().isEmpty()) {

            var items = invoiceRequest.getItems();
            items.setInvoiceId(invoiceId);

            for (ItemRequest i : items.getItemsRequest()) {
                validateItem(i);
            }

            List<ItemResponse> savedItems = itemService.addItems(items);

            List<Long> itemIds = savedItems.stream()
                    .map(ItemResponse::getId)
                    .toList();

            for (ItemResponse item : savedItems) {
                addInvoiceToOperation(
                        invoiceId,
                        "Item added: " + item.getProductName() + ", item size: " + itemIds.size(),
                        OperationStatus.DRAFT,
                        List.of(item.getId())
                );
            }

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

            List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
            List<Long> itemIds = items == null
                    ? List.of()
                    : items.stream()
                    .map(ItemResponse::getId)
                    .filter(Objects::nonNull)
                    .toList();

            addInvoiceToOperation(invoiceId, "Invoice approved", OperationStatus.APPROVED, itemIds.isEmpty() ? null : itemIds);
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

            List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
            List<Long> itemIds = items == null ? List.of()
                    : items.stream().map(ItemResponse::getId).filter(Objects::nonNull).toList();

            if (!itemIds.isEmpty()) {
                itemService.deleteItemsByItemsId(itemIds);
                for (Long itemId : itemIds) {
                    addInvoiceToOperation(invoiceEntity.getId(), "Invoice canceled", OperationStatus.CANCELED, List.of(itemId));
                }
            } else {
                addInvoiceToOperation(invoiceEntity.getId(), "Invoice canceled", OperationStatus.CANCELED, null);
            }
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

        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
        List<Long> itemIds = (items == null) ? List.of()
                : items.stream().map(ItemResponse::getId).filter(Objects::nonNull).toList();

        String opComment = (request.getComment() == null || request.getComment().isBlank())
                ? "Correction requested"
                : request.getComment();

        if (!itemIds.isEmpty()) {
            for (Long itemId : itemIds) {
                addInvoiceToOperation(invoiceEntity.getId(), opComment, OperationStatus.CORRECTION, List.of(itemId));
            }
        } else {
            addInvoiceToOperation(invoiceEntity.getId(), opComment, OperationStatus.CORRECTION, null);
        }
    }

    private void addInvoiceToOperation(Long invoiceId, String comment, OperationStatus status, List<Long> itemIds) {
        InvoiceEntity invoiceEntity = fetchInvoiceIfExist(invoiceId);

        List<CreateOperationDetailsRequest> items = itemIds != null
                ? itemIds.stream()
                .map(itemId -> {
                    itemService.findById(itemId);
                    return CreateOperationDetailsRequest.builder()
                            .itemId(itemId)
                            .itemStatus(ItemStatus.CREATED)
                            .comment(comment)
                            .build();
                })
                .toList()
                : Collections.emptyList();

        CreateOperationRequest operationRequest = CreateOperationRequest.builder()
                .status(status)
                .invoiceId(invoiceEntity.getId())
                .items(items)
                .build();

        operationService.saveOperation(operationRequest);

    }

    @Override
    public InvoiceResponse findById(Long id) {
        var invoiceEntity = fetchInvoiceIfExist(id);
        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    @Override
    public List<InvoiceResponse> findAllByRecipientUserTaxId(String recipientTaxId) {
        var userResponse = findRecipientByTaxId(recipientTaxId);
        var allByRecipientUserTaxId = invoiceRepository.findAllInvoicesByRecipientUserTaxId(userResponse.getTaxId());
        return invoiceMapper.allByRecipientUserTaxId(allByRecipientUserTaxId);
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
    public List<InvoiceResponse> findInvoicesBySenderTaxId(String senderTaxId) {
        //TODO: check senderTaxId equals to current user ID
        return invoiceRepository.findInvoicesBySenderTaxId(senderTaxId)
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
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
            itemService.updateItem(request.getUpdatedItems());
        }

        if (!isNullOrEmpty(request.getDeletedItemsId())) {
//            itemService.deleteItem(request.getDeletedItemsId());
        }

        //TODO: update invoice updatedAt field when one of items related with invoice updated

        invoiceRepository.refreshInvoice(request.getInvoiceId());
        invoice = fetchInvoiceIfExist(request.getInvoiceId());

        addInvoiceToOperation(request.getInvoiceId(), "Invoice updated", UPDATE, null);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    private boolean doesntMatchInvoiceStatus(InvoiceEntity invoice, InvoiceStatus... statuses) {
        return !Arrays.asList(statuses).contains(invoice.getStatus());
    }

}
