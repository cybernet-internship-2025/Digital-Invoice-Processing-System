package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/internal/invoices")
public class InvoiceController {
    InvoiceService invoiceService;

    @GetMapping
    List<InvoiceResponse> findAll() {
        return invoiceService.findAll();
    }

    @GetMapping("/find-by-sender-tax-id/{senderTaxId}")
    public List<InvoiceResponse> findInvoicesBySenderTaxId(@PathVariable("senderTaxId") String senderTaxId) {
        return invoiceService.findInvoicesBySenderTaxId(senderTaxId);
    }


    @PutMapping("/{recipientTaxId}/{invoiceId}")
    public InvoiceResponse updateInvoiceRecipientId(@PathVariable("recipientTaxId") String recipientTaxId,
                                                    @PathVariable("invoiceId") Long invoiceId) {
        return invoiceService.updateInvoiceRecipientId(recipientTaxId, invoiceId);
    }

    @PutMapping("/send-invoice/{invoiceId}")
    public InvoiceResponse sendInvoice(@PathVariable("invoiceId") Long invoiceId,
                                       @RequestBody SendInvoiceRequest request) {
        return invoiceService.sendInvoice(invoiceId, request);
    }

    @PutMapping("/send-invoice-to-correction/{invoiceId}/{senderTaxId}")
    public InvoiceResponse sendInvoiceToCorrection(@PathVariable("invoiceId") Long invoiceId,
                                                   @PathVariable("senderTaxId") String senderTaxId) {
        return invoiceService.sendInvoiceToCorrection(invoiceId, senderTaxId);
    }

    @PutMapping("/rollback/{invoiceId}/{senderTaxId}")
    public InvoiceResponse rollbackInvoice(@PathVariable("invoiceId") Long invoiceId,
                                           @PathVariable("senderTaxId") String senderTaxId) {
        return invoiceService.rollbackInvoice(invoiceId, senderTaxId);
    }


    @DeleteMapping("/{invoiceId}")
    public void deleteInvoiceById(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.deleteInvoiceById(invoiceId);
    }


}