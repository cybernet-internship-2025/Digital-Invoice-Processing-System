package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.repository.OperationRepository;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.service.abstraction.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static az.cybernet.invoice.exception.ExceptionConstants.INVOICE_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.SENDER_NOT_FOUND;
import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    OperationRepository operationRepository;
    InvoiceRepository invoiceRepository;
    UserClient userClient;
    InvoiceMapper invoiceMapper;
    ItemService itemService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest) {
        var sender = findSenderByTaxId(invoiceRequest.getSenderTaxId());
        var recipient = findRecipientByTaxId(invoiceRequest.getRecipientTaxId());

        var invoiceResponse = invoiceMapper.buildInvoiceResponse(invoiceRequest);
        invoiceResponse.setSenderTaxId(sender.getTaxId());
        invoiceResponse.setRecipientTaxId(recipient.getTaxId());
        invoiceResponse.setTotalPrice(calculateTotalPrice());
        invoiceResponse.setInvoiceNumber(generateInvoiceNumber());
        invoiceResponse.setInvoiceSeries("INVD");

        invoiceRepository.saveInvoice(invoiceMapper.buildInvoiceEntity(invoiceResponse));
        return invoiceResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ItemResponse> addItemsToInvoice(ItemsRequest request) {
        InvoiceEntity invoice = fetchInvoiceIfExist(request.getInvoiceId());

        List<ItemResponse> responseList = new ArrayList<>();

        for (ItemRequest ir : request.getItemsRequest()) {

            ItemEntity newItem = ItemEntity.builder()
                    .name(ir.getProductName())
                    .unitPrice(ir.getUnitPrice())
                    .quantity(ir.getQuantity())
                    .measurement(
                            MeasurementEntity.builder()
                                    .name(ir.getMeasurementName())
                                    .build())
                    .invoice(invoice)
                    .isActive(true)
                    .build();

//            ItemResponse itemResponse = itemService.saveItem(newItem);
//            responseList.add(itemResponse);
        }

//        updateInvoiceTotalPrice(invoice.getId());
        return responseList;
    }

    private InvoiceEntity fetchInvoiceIfExist(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException(INVOICE_NOT_FOUND.getCode(), INVOICE_NOT_FOUND.getMessage()));
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

    private BigDecimal calculateTotalPrice() {
        return ZERO;
    }

//    public void updateInvoiceTotalPrice(Long invoiceId) {
//        List<ItemEntity> items = itemRepository.findAllByInvoiceId(invoiceId);
//
//        BigDecimal total = items.stream()
//                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        invoiceRepository.updateTotalPrice(invoiceId, total);
//    }

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

    @Override
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.getAll()
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }
}
