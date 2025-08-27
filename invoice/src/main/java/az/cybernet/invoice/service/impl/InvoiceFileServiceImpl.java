package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.constants.InvoiceHeaders;
import az.cybernet.invoice.constants.InvoiceRecievedHeaders;
import az.cybernet.invoice.dto.request.invoice.InvoiceExportRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.service.abstraction.InvoiceFileService;
import az.cybernet.invoice.util.ExcelFileExporter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceFileServiceImpl implements InvoiceFileService {
    private final InvoiceServiceImpl invoiceService;
    private final ExcelFileExporter excelFileExporter;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;

    public InvoiceFileServiceImpl(InvoiceServiceImpl invoiceService, ExcelFileExporter excelFileExporter, InvoiceMapper invoiceMapper, InvoiceRepository invoiceRepository) {
        this.invoiceService = invoiceService;
        this.excelFileExporter = excelFileExporter;
        this.invoiceMapper = invoiceMapper;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public byte[] exportInvoiceToExcel(InvoiceFilterRequest request, String taxId) {
        String[] headers = InvoiceHeaders.HEADERS;
        List<FilterResponse> invoices = invoiceService.findInvoicesBySenderTaxId(taxId, request);
        return ExcelFileExporter.exportInvoicesToExcel(invoices, headers);

    }
    @Override
    public byte[] exportRecipientInvoicesToExcel(InvoiceExportRequest request, String taxId) {
        String[] headers = InvoiceRecievedHeaders.HEADERS;
        var userResponse = invoiceService.findRecipientByTaxId(request.getRecipientTaxId());

        var entities = invoiceRepository
                .findAllInvoicesByRecipientUserTaxId(userResponse.getTaxId(), invoiceMapper.map(request));

        return excelFileExporter.writeRecipientInvoicesToExcel(entities, headers);
    }

}
