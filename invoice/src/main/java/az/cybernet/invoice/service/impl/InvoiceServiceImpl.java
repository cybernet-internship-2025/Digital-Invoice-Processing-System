package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.exception.UserNotFoundException;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.SENDER_NOT_FOUND;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    UserClient userClient;
    InvoiceMapper invoiceMapper;

    @Override
    public InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest) {
        UserResponse sender = userClient.findUserByTaxId(invoiceRequest.getSenderTaxId());
        if (sender == null) {
            throw new UserNotFoundException(SENDER_NOT_FOUND.getCode(), SENDER_NOT_FOUND.getMessage());
        }

        UserResponse recipient = userClient.findUserByTaxId(invoiceRequest.getRecipientTaxId());
        if (recipient == null) {
            throw new UserNotFoundException(RECIPIENT_NOT_FOUND.getCode(), RECIPIENT_NOT_FOUND.getMessage());
        }

        InvoiceResponse invoiceResponse = invoiceMapper.toResponse(invoiceRequest);
        invoiceResponse.setTotalPrice(calculateTotalPrice());
        invoiceResponse.setInvoiceNumber(generateInvoiceNumber());
        invoiceResponse.setInvoiceSeries("INVD");

//        invoiceRepository.saveInvoice();
        return invoiceResponse;
    }

    public BigDecimal calculateTotalPrice() {
        return BigDecimal.ZERO;
    }

    public String generateInvoiceNumber() {
        return "";
    }

    @Override
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.getAll()
                .stream()
                .map(invoiceMapper::fromEntityToResponse)
                .toList();
    }
}
