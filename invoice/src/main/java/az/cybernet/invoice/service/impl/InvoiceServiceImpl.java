package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static az.cybernet.invoice.enums.InvoiceStatus.APPROVED;
import static az.cybernet.invoice.enums.InvoiceStatus.PENDING;
import static az.cybernet.invoice.exception.ExceptionConstants.INVALID_STATUS;
import static az.cybernet.invoice.exception.ExceptionConstants.INVOICE_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.SENDER_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.UNAUTHORIZED;
import static az.cybernet.invoice.enums.InvoiceStatus.*;
import static az.cybernet.invoice.exception.ExceptionConstants.*;
import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

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
        var sender = findSenderByTaxId(invoiceRequest.getSenderTaxId());
        var recipient = findRecipientByTaxId(invoiceRequest.getRecipientTaxId());

        var invoiceEntity = invoiceMapper.fromInvoiceRequestToEntity(invoiceRequest);

        invoiceEntity.setSenderTaxId(sender.getTaxId());
        invoiceEntity.setRecipientTaxId(recipient.getTaxId());
        invoiceEntity.setInvoiceNumber(generateInvoiceNumber());

        invoiceRepository.saveInvoice(invoiceEntity);
        return invoiceMapper.fromInvoiceEntityToResponse(invoiceEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ItemResponse> addItemsToInvoice(ItemsRequest requests) {
        fetchInvoiceIfExist(requests.getInvoiceId());
        List<ItemResponse> items = itemService.addItems(requests);
        updateInvoiceTotalPrice(requests.getInvoiceId());
        return items;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approveInvoice(Long invoiceId, String senderTaxId, String recipientTaxId) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(recipientTaxId) || !invoiceEntity.getSenderTaxId().equals(senderTaxId)) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(APPROVED);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        addInvoiceCommentToOperation(invoiceEntity.getId(), recipientTaxId, null, OperationStatus.APPROVED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelInvoice(Long invoiceId, String senderTaxId, String recipientTaxId) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(recipientTaxId) || !invoiceEntity.getSenderTaxId().equals(senderTaxId)) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(InvoiceStatus.CANCELED);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        addInvoiceCommentToOperation(invoiceEntity.getId(), recipientTaxId, null, OperationStatus.CANCELED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void requestCorrection(Long invoiceId, String senderTaxId, String recipientTaxId, String comment) {
        var invoiceEntity = fetchInvoiceIfExist(invoiceId);

        if (!invoiceEntity.getRecipientTaxId().equals(recipientTaxId) || !invoiceEntity.getSenderTaxId().equals(senderTaxId)) {
            throw new UnauthorizedException(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }

        if (invoiceEntity.getStatus() != PENDING) {
            throw new InvalidStatusException(INVALID_STATUS.getCode(), INVALID_STATUS.getMessage());
        }

        invoiceEntity.setStatus(InvoiceStatus.CORRECTION);
        invoiceEntity.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.saveInvoice(invoiceEntity);

        addInvoiceCommentToOperation(invoiceEntity.getId(), recipientTaxId, comment, OperationStatus.CORRECTION);
    }

    private void addInvoiceCommentToOperation(Long invoiceId, String taxId, String comment, OperationStatus status) {
        InvoiceEntity invoiceEntity = fetchInvoiceIfExist(invoiceId);
        CreateOperationRequest operationRequest = CreateOperationRequest.builder()
                .taxId(taxId)
                .comment(comment)
                .status(status)
                .invoice(invoiceEntity)
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

        addInvoiceCommentToOperation(invoiceEntity.getId(), invoiceEntity.getRecipientTaxId(), null, OperationStatus.DRAFT);
    }

    @Override
    public List<InvoiceResponse> findAllByRecipientUserTaxId(String recipientTaxId) {
        var userResponse = findRecipientByTaxId(recipientTaxId);
        var allByRecipientUserTaxId = invoiceRepository.findAllByRecipientUserTaxId(userResponse.getTaxId());
        return invoiceMapper.allByRecipientUserTaxId(allByRecipientUserTaxId);
    }

    private void updateInvoiceTotalPrice(Long invoiceId) {
        List<ItemResponse> items = itemService.findAllItemsByInvoiceId(invoiceId);

        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoiceRepository.updateTotalPrice(invoiceId, total);
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
        if (lastNumber != null && lastNumber.length() == 8) {
            String lastSequence = lastNumber.substring(4);
            try {
                nextSequence = Integer.parseInt(lastSequence) + 1;
            } catch (NumberFormatException e) {
                nextSequence = 1;
            }
        }

        var sequence = String.format("%04d", nextSequence);
        return prefix + "-" + sequence;
    }

    private InvoiceEntity fetchInvoiceIfExist(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));
    }

    @Override
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.getAll()
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteInvoiceById(Long id) {
        fetchInvoiceIfExist(id);
        invoiceRepository.deleteInvoiceById(id);
    }

    @Override
    @Transactional
    public InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId) {
        //TODO: check current user has invoice given by ID or not
        InvoiceEntity invoice = fetchInvoiceIfExist(invoiceId);

        if(!(invoice.getStatus().equals(DRAFT)||invoice.getStatus().equals(CORRECTION))){
            throw new RuntimeException("You cannot update invoice if it isn't in DRAFT status!");
        }

        invoice.setRecipientTaxId(recipientTaxId);

        invoiceRepository.updateInvoice(invoice);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoice(Long invoiceId, SendInvoiceRequest request) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndBySenderTaxId(invoiceId,request.getSenderUserTaxId())
                        .orElseThrow(()->new RuntimeException("You have no any invoice on status DRAFT and given by ID!"));

        invoiceRepository.changeStatus(invoiceId, PENDING.toString());
        invoice.setStatus(PENDING);

        //TODO: create operation

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> findAllByStatus(String status) {
        return invoiceRepository.findAllByStatus(status)
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public InvoiceResponse sendInvoiceToCorrection(Long invoiceId, String receiverTaxId) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndReceiverTaxId(invoiceId, receiverTaxId)
                .orElseThrow(()->new RuntimeException("You have not any received active invoice on status PENDING and given by ID!"));

        invoiceRepository.changeStatus(invoiceId, "CORRECTION");

        invoice.setStatus(CORRECTION);

        //TODO: create operation

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    public InvoiceResponse rollbackInvoice(Long invoiceId,String senderTaxId) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndBySenderTaxId(invoiceId,senderTaxId)
                .orElseThrow(()->new RuntimeException("You have not any invoice on status PENDING and given by ID"));

        invoice.setStatus(DRAFT);

        //TODO: create operation

        invoiceRepository.changeStatus(invoiceId, "DRAFT");

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> findInvoicesBySenderTaxId(Long senderTaxId) {
        return null;
    }

}
