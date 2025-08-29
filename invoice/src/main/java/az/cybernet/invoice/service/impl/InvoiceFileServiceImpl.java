package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.constants.InvoiceHeaders;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.service.abstraction.InvoiceFileService;
import az.cybernet.invoice.util.ExcelFileExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class InvoiceFileServiceImpl implements InvoiceFileService {
    private final InvoiceServiceImpl invoiceService;
    private final ExcelFileExporter excelFileExporter;


    @Override
    public byte[] exportInvoiceToExcel(InvoiceFilterRequest request, String taxId) {
        String[] headers = InvoiceHeaders.HEADERS;
        List<FilterResponse> invoices = invoiceService.findInvoicesBySenderTaxId(taxId, request);
        return ExcelFileExporter.exportInvoicesToExcel(invoices, headers);

    }

}
