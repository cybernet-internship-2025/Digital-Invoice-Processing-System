package az.cybernet.filestorage.controller;

import az.cybernet.filestorage.client.InvoiceClient;
import az.cybernet.filestorage.dto.client.InvoiceResponse;
import az.cybernet.filestorage.service.PDFGeneratorService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invoice")
public class InvoicePdfController {
    private final PDFGeneratorService pdfGeneratorService;
    private final InvoiceClient invoiceClient;

    @GetMapping(value = "/{invoiceId}/receipt.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void getInvoicePdf(@PathVariable Long invoiceId, HttpServletResponse response) {
        try {
            InvoiceResponse invoice = invoiceClient.findInvoiceById(invoiceId);
            pdfGeneratorService.export(response, invoice);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }
}
