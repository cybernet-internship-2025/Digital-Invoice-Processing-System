package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.create.CreateInvoiceRequest;
import az.cybernet.invoice.dto.response.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

}
