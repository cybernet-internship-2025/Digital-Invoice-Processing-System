package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.mapper.OperationMapStruct;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.repository.OperationRepository;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    OperationRepository operationRepository;
    InvoiceRepository invoiceRepository;
    OperationMapStruct operationMapStruct;
    InvoiceMapper invoiceMapper;
    @Override
    public InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest) {
        return null;
    }
}
