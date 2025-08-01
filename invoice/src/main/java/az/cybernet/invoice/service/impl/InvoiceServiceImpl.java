package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.enums.InvoiceStatus;
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
import java.util.List;
import java.util.Objects;

import static az.cybernet.invoice.enums.InvoiceStatus.APPROVED;
import static az.cybernet.invoice.enums.InvoiceStatus.CORRECTION;
import static az.cybernet.invoice.enums.InvoiceStatus.PENDING;
import static az.cybernet.invoice.enums.OperationStatus.DRAFT;
import static az.cybernet.invoice.exception.ExceptionConstants.INVALID_STATUS;
import static az.cybernet.invoice.exception.ExceptionConstants.INVOICE_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.SENDER_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.UNAUTHORIZED;
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
        invoiceEntity.setTotalPrice(ZERO);

        invoiceRepository.saveInvoice(invoiceEntity);
        Long invoiceId = invoiceEntity.getId();

        addInvoiceToOperation(invoiceId, "Invoice created", OperationStatus.DRAFT, null);

        if (invoiceRequest.getItems() != null && invoiceRequest.getItems().getItemsRequest() != null && !invoiceRequest.getItems().getItemsRequest().isEmpty()) {

            var items = invoiceRequest.getItems();
            items.setInvoiceId(invoiceId);

            for (ItemRequest i : items.getItemsRequest()) {
                validateItem(i);
            }

            List<ItemResponse> savedItems = itemService.addItems(items);

            List<Long> itemIds = savedItems.stream()
                    .map(ItemResponse::getId)
                    .toList();

            addInvoiceToOperation(invoiceId,"Items added: " + itemIds.size(), OperationStatus.DRAFT, itemIds);

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

        BigDecimal total = items.stream()
                .map(ItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoiceRepository.updateTotalPrice(invoiceId, total);
        return total;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approveInvoice(Long invoiceId, ApproveAndCancelInvoiceRequest request) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId()) || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(APPROVED);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
        List<Long> itemIds = items == null ? List.of()
                : items.stream().map(ItemResponse::getId).filter(Objects::nonNull).toList();

        addInvoiceToOperation(invoiceEntity.getId(), request.getRecipientTaxId(), "Invoice approved", OperationStatus.APPROVED, itemIds.isEmpty() ? null : itemIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelInvoice(Long invoiceId, ApproveAndCancelInvoiceRequest request) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId()) || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(InvoiceStatus.CANCELED);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        addInvoiceToOperation(invoiceEntity.getId(), "Invoice canceled", OperationStatus.CANCELED, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void requestCorrection(Long invoiceId, RequestCorrectionRequest request) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(request.getRecipientTaxId()) || !invoiceEntity.getSenderTaxId().equals(request.getSenderTaxId())) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(InvoiceStatus.CORRECTION);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);
        List<Long> itemIds = (items == null) ? List.of()
                : items.stream().map(ItemResponse::getId).filter(Objects::nonNull).toList();

        String opComment = (request.getComment() == null || request.getComment().isBlank())
                ? "Correction requested"
                : request.getComment();

        addInvoiceToOperation(invoiceEntity.getId(), opComment, OperationStatus.CORRECTION, itemIds.isEmpty() ? null : itemIds);
    }

    private void addInvoiceToOperation(Long invoiceId, String comment, OperationStatus status, List<Long> itemIds) {
        InvoiceEntity invoiceEntity = fetchInvoiceIfExist(invoiceId);
        CreateOperationRequest operationRequest = CreateOperationRequest.builder()
                .comment(comment)
                .status(status)
                .invoiceId(invoiceEntity.getId())
                .itemIds(itemIds)
                .build();

        operationService.saveOperation(operationRequest);
    }

    @Override
    public InvoiceResponse findById(Long id) {
        var invoiceEntity = fetchInvoiceIfExist(id);
        return invoiceMapper.fromEntityToResponse(invoiceEntity);
    }

    @Override
    public void restoreInvoice(Long id) {
        var invoiceEntity = fetchInvoiceIfExist(id);
        invoiceRepository.restoreInvoice(invoiceEntity.getId());

        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceEntity.getId());
        List<Long> itemIds = items == null ? List.of()
                : items.stream().map(ItemResponse::getId).filter(Objects::nonNull).toList();

        addInvoiceToOperation(invoiceEntity.getId(), null, DRAFT, itemIds.isEmpty() ? null : itemIds);
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

        var lastNumber = invoiceRepository.findLastInvoiceNumberStartingWith(prefix);

        int nextSequence = 1;
        if (lastNumber != null && lastNumber.length() == 9) {
            String lastSequence = lastNumber.substring(5);
            try {
                nextSequence = Integer.parseInt(lastSequence) + 1;
            } catch (NumberFormatException e) {
                nextSequence = 1;
            }
        }

        var sequence = String.format("%04d", nextSequence);
        return prefix + sequence;
    }

    private InvoiceEntity fetchInvoiceIfExist(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));
    }

    @Override
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteInvoiceById(Long invoiceId) {
        fetchInvoiceIfExist(invoiceId);
        invoiceRepository.deleteInvoiceById(invoiceId);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId) {
        //TODO: check current user has invoice given by ID or not
        InvoiceEntity invoice = fetchInvoiceIfExist(invoiceId);

        if (!(invoice.getStatus().equals(DRAFT) || invoice.getStatus().equals(CORRECTION))) {
            throw new RuntimeException("You cannot update invoice if it isn't on DRAFT or CORRECTION status!");
        }

        //TODO: check exists user given by recipientTaxId
        invoice.setRecipientTaxId(recipientTaxId);

        invoiceRepository.updateInvoiceRecipientTaxId(recipientTaxId);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoice(Long invoiceId, SendInvoiceRequest request) {
        InvoiceEntity invoice = invoiceRepository.findBySenderTaxIdAndInvoiceId(request.getSenderUserTaxId(), invoiceId)
                        .orElseThrow(()->new RuntimeException("You have no any invoice given by ID!"));

        if(!(invoice.getStatus() == InvoiceStatus.DRAFT || invoice.getStatus() == CORRECTION)){
            throw new RuntimeException("Your invoice isn't on DRAFT status!");
        }

        invoiceRepository.changeStatus(invoiceId, PENDING.toString());
        invoice.setStatus(PENDING);

        //TODO: create operation

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoiceToCorrection(Long invoiceId, String receiverTaxId) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndReceiverTaxId(invoiceId, receiverTaxId)
                .orElseThrow(()->new RuntimeException("You have not any received active invoice and given by ID!"));

        if(invoice.getStatus() != PENDING){
            throw new RuntimeException("You have not any received active invoice on status PENDING");
        }

        invoiceRepository.changeStatus(invoiceId, "CORRECTION");

        invoice.setStatus(CORRECTION);

        //TODO: create operation

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse rollbackInvoice(Long invoiceId,String senderTaxId) {
        InvoiceEntity invoice = invoiceRepository.findBySenderTaxIdAndInvoiceId(senderTaxId, invoiceId)
                .orElseThrow(()->new RuntimeException("You have not any invoice on status PENDING and given by ID"));

        invoice.setStatus(InvoiceStatus.DRAFT);

        //TODO: create operation

        invoiceRepository.changeStatus(invoiceId, "DRAFT");

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

}
