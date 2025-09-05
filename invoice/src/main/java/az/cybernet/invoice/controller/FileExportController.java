package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.InvoiceExportRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceFileService;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.service.abstraction.PDFGeneratorService;
import az.cybernet.invoice.util.ExcelFileExporter;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/utils")
@RequiredArgsConstructor
public class FileExportController {
    private final ExcelFileExporter excelFileExporter;
    private final InvoiceFileService invoiceFileService;
    private final InvoiceService invoiceService;
    private final PDFGeneratorService pdfGeneratorService;

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

    @GetMapping(value = "/{invoiceId}/receipt.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void getInvoicePdf(@PathVariable Long invoiceId, HttpServletResponse response) {
        try {
            InvoiceResponse invoice = invoiceService.findById(invoiceId);
            pdfGeneratorService.export(response, invoice);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }
}
