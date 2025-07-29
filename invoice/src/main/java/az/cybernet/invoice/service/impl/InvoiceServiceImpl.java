package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceRequest;
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
