package az.cybernet.filestorage.controller;

import az.cybernet.filestorage.service.impl.InvoicePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invoice")
public class InvoicePdfController {
    private final InvoicePdfService invoicePdfService;

    @GetMapping("/{id}/receipt.pdf")
    public ResponseEntity<String> getInvoicePdf(Long invoiceId) {
        invoicePdfService.generateInvoicePdf(invoiceId);
        return ResponseEntity.ok("PDF generated successfully");
    }
}
