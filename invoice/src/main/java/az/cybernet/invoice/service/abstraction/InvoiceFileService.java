package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.InvoiceExportRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import org.springframework.stereotype.Service;


public interface InvoiceFileService {
    byte[] exportInvoiceToExcel(InvoiceFilterRequest request, String taxId);

    byte[] exportRecipientInvoicesToExcel(InvoiceExportRequest request, String taxId);
}
