package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.aop.annotation.Log;
import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ReturnItemRequest;
import az.cybernet.invoice.dto.request.operation.AddItemsToOperationRequest;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static az.cybernet.invoice.enums.InvoiceStatus.APPROVED;
import static az.cybernet.invoice.enums.InvoiceStatus.CORRECTION;
import static az.cybernet.invoice.enums.InvoiceStatus.DRAFT;
import static az.cybernet.invoice.enums.InvoiceStatus.PENDING;
import static az.cybernet.invoice.enums.OperationStatus.DELETE;
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
@FieldDefaults(level = PRIVATE)
public class InvoiceServiceImpl implements InvoiceService {
    final InvoiceRepository invoiceRepository;
    final UserClient userClient;
    final InvoiceMapper invoiceMapper;
    final ItemService itemService;
    final OperationService operationService;
    static int MAX_SIZE = 50;
    static int MIN_SIZE = 10;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            UserClient userClient,
            InvoiceMapper invoiceMapper,
            OperationService operationService,
            @Lazy ItemService itemService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.userClient = userClient;
        this.invoiceMapper = invoiceMapper;
        this.operationService = operationService;
        this.itemService = itemService;
    }

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

        List<Long> itemIds = itemResponses.stream()
                .map(ItemResponse::getId)
                .collect(Collectors.toList());

        AddItemsToOperationRequest items = AddItemsToOperationRequest.builder()
                .invoiceId(invoiceEntity.getId())
                .comment(opComment)
                .status(OperationStatus.CORRECTION)
                .itemIds(itemIds)
                .build();

        itemService.addItemsToOperation(items);
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

    @Override
    public void exportReceivedInvoicesToExcel(InvoiceExportRequest request,
                                              HttpServletResponse response) {
        var userResponse = findRecipientByTaxId(request.getRecipientTaxId());
        var entities = invoiceRepository
                .findAllInvoicesByRecipientUserTaxId(userResponse.getTaxId(), invoiceMapper.map(request));
        writeInvoicesToExcel(entities, response);

    }

    private void writeInvoicesToExcel(List<InvoiceEntity> invoices, HttpServletResponse response) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("invoices");

            CellStyle headerStyle = wb.createCellStyle();
            Font bold = wb.createFont();
            bold.setBold(true);
            headerStyle.setFont(bold);

            String[] headers = {
                    "Created At", "Series", "Number", "Status",
                    "Sender Tax ID", "Recipient Tax ID", "Total Price", "Item Count"
            };
            Row h = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            int rowIdx = 1;
            for (InvoiceEntity inv : invoices) {

                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(inv.getCreatedAt() != null ? dtf.format(inv.getCreatedAt()) : "");
                r.createCell(1).setCellValue(inv.getInvoiceSeries() != null ? inv.getInvoiceSeries() : "");
                r.createCell(2).setCellValue(inv.getInvoiceNumber() != null ? inv.getInvoiceNumber() : "");
                r.createCell(3).setCellValue(inv.getStatus() != null ? inv.getStatus().name() : "");
                r.createCell(4).setCellValue(inv.getSenderTaxId() != null ? inv.getSenderTaxId() : "");
                r.createCell(5).setCellValue(inv.getRecipientTaxId() != null ? inv.getRecipientTaxId() : "");
                r.createCell(6).setCellValue(inv.getTotalPrice() != null ? inv.getTotalPrice().doubleValue() : 0.0d);
                r.createCell(7).setCellValue(inv.getItems() != null ? inv.getItems().size() : 0);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            String fileName = URLEncoder.encode("invoices_received.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

            wb.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export invoices to Excel", e);
        }
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvoiceById(DeleteInvoicesRequest request) {
        List<Long> invalidIds = invoiceRepository.findInvalidInvoiceIdsBySenderTaxId(request.getSenderTaxId(), request.getInvoicesIds());

        if (!invalidIds.isEmpty()) {
            throw new RuntimeException("These invoice is not yours: " + invalidIds);
        }

        invoiceRepository.findInvoicesByIds(request.getInvoicesIds())
                .forEach(invoice -> {
                            doesntMatchInvoiceStatus(invoice, InvoiceStatus.DRAFT);
                            addInvoiceToOperation(invoice.getId(), "Invoice deleted", DELETE);
                        }
                );

        invoiceRepository.deleteInvoicesById(request.getInvoicesIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceResponse updateInvoiceRecipientTaxId(UpdateInvoiceRecipientTaxIdRequest request) {
        //TODO: check current user has invoice given by ID or not
        InvoiceEntity invoice = fetchInvoiceIfExist(request.getInvoiceId());

        doesntMatchInvoiceStatus(invoice, CORRECTION, InvoiceStatus.DRAFT);
        findRecipientByTaxId(request.getRecipientTaxId());

        invoice.setRecipientTaxId(request.getRecipientTaxId());
        invoice.setStatus(PENDING);

        invoiceRepository.updateInvoiceRecipientTaxId(request);

        addInvoiceToOperation(request.getInvoiceId(), "Recipient Tax ID changed to: " + request.getRecipientTaxId(), OperationStatus.PENDING);

        return invoiceMapper.fromEntityToResponse(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<InvoiceResponse> sendInvoice(SendInvoiceRequest request) {
        List<Long> invalidIds = invoiceRepository.findInvalidInvoiceIdsBySenderTaxId(request.getSenderUserTaxId(), request.getInvoiceIds());

        if (!invalidIds.isEmpty()) {
            throw new RuntimeException("These invoice is not yours: " + invalidIds);
        }

        List<InvoiceEntity> invoices = invoiceRepository.findInvoicesByIds(request.getInvoiceIds());

        invoices.forEach(invoice -> {
            doesntMatchInvoiceStatus(invoice, CORRECTION, InvoiceStatus.DRAFT);
            invoice.setStatus(PENDING);
            addInvoiceToOperation(invoice.getId(), "Invoice with id " + invoice.getId() + " sent", OperationStatus.PENDING);
        });

        invoiceRepository.updateStatuses(request.getInvoiceIds(), PENDING.toString());

        return invoices.stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }


    @Override
    public PagedResponse<InvoiceResponse> findInvoicesBySenderTaxId(String senderTaxId, InvoiceFilterRequest filter) {
        findSenderByTaxId(senderTaxId);

        int queryLimit = filter.getLimit() + 1;
        filter.setLimit(queryLimit);

        List<InvoiceEntity> entities = invoiceRepository.findInvoicesBySenderTaxId(senderTaxId, filter);

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
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));

        doesntMatchInvoiceStatus(invoice, CORRECTION, DRAFT);

        addInvoiceToOperation(request.getInvoiceId(), request.getComment(), UPDATE);

        if (!isNullOrEmpty(request.getCreatedItems().getItemsRequest())) {
            itemService.addItems(request.getCreatedItems());
        }

        if (!isNullOrEmpty(request.getUpdatedItems())) {
            itemService.updateItems(request.getUpdatedItems(), request.getInvoiceId());
        }

        if (!isNullOrEmpty(request.getDeletedItemsId())) {
            itemService.deleteItemsByItemsId(request.getDeletedItemsId());
        }

        invoiceRepository.refreshInvoice(request.getInvoiceId());
        invoice = fetchInvoiceIfExist(request.getInvoiceId());

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

    private void doesntMatchInvoiceStatus(InvoiceEntity invoice, InvoiceStatus... statuses) {
        if (!Arrays.asList(statuses).contains(invoice.getStatus())) {
            String statusList = Arrays.stream(statuses)
                    .map(Enum::name)
                    .collect(Collectors.joining(" or "));
            throw new RuntimeException("Invoice status must be one of: " + statusList);
        }
    }

    @Override
    @Transactional
    public void markAsPending(Long invoiceId, String comment) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice == null) {
            throw new RuntimeException("Invoice not found");
        }

        invoice.setPreviousStatus(invoice.getStatus());
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setLastPendingAt(LocalDateTime.now());
        invoice.setComment(comment);

//        invoiceRepository.updateInvoiceStatus(invoice);
    }


    @Override
    @Transactional
    public void approvePendingInvoicesAfterTimeout() {
        LocalDateTime deadline = LocalDateTime.now().minusMonths(1);
//        List<InvoiceEntity> expiredInvoices = invoiceRepository.findPendingInvoicesOlderThan(deadline);

//        for (InvoiceEntity invoice : expiredInvoices) {
//            invoiceRepository.approveInvoiceById(invoice.getId());
//        }

    }

    @Override
    public void sendInvoiceToCancel(Long invoiceId, String receiverTaxId) {
        InvoiceEntity invoice = invoiceRepository.findByIdAndReceiverTaxId(invoiceId, receiverTaxId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));

        doesntMatchInvoiceStatus(invoice, PENDING);

        invoiceRepository.updateStatuses(List.of(invoiceId), "SEND_TO_CANCEL");

        addInvoiceToOperation(invoiceId, "Invoice sent to cancel", OperationStatus.SEND_TO_CANCEL);
    }

    @Override
    public void cancelPendingInvoicesAfterTimeout() {
        List<Long> invoiceIds = invoiceRepository.findInvoicePendingMoreThanOneMonth()
                .stream()
                .map(InvoiceEntity::getId)
                .toList();

        invoiceRepository.updateStatuses(invoiceIds, "SEND_TO_CANCEL");
    }

}
