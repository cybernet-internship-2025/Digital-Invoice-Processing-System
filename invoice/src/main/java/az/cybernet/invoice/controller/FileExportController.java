package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.InvoiceExportRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.service.abstraction.InvoiceFileService;
import az.cybernet.invoice.util.ExcelFileExporter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/utils")
@RequiredArgsConstructor
public class FileExportController {
    private final ExcelFileExporter excelFileExporter;
    private final InvoiceFileService invoiceFileService;

    @PostMapping("/{taxId}/sent/export-to-excelexport")
    public ResponseEntity<byte[]> exportSentInvoiceToExcel(
            @PathVariable("taxId") String taxId,
            @RequestBody @Valid InvoiceFilterRequest invoiceFilterRequest,
            @RequestParam(value = "fileName", defaultValue = "Invoice") String fileName) {
        return excelFileExporter.buildExcelResponse(
                invoiceFileService.exportInvoiceToExcel(invoiceFilterRequest, taxId),
                fileName
        );
    }

    @PostMapping("/{taxId}/recipient/export-to-excel")
    public ResponseEntity<byte[]> exportRecipientInvoiceToExcel(
            @PathVariable("taxId") String taxId,
            @RequestBody InvoiceExportRequest request) {

        return excelFileExporter
                .buildExcelResponse(invoiceFileService
                        .exportRecipientInvoicesToExcel(request, taxId), "invoices_received");
    }
}
