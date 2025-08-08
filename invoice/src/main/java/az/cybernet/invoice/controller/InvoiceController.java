package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceToCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api/v1/invoices")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceController {
    InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(CREATED)
    public InvoiceResponse saveInvoice(@RequestBody CreateInvoiceRequest invoiceRequest) {
        return invoiceService.saveInvoice(invoiceRequest);
    }

    @PostMapping("/approve")
    @ResponseStatus(NO_CONTENT)
    public void approveInvoice(@RequestBody @Valid ApproveAndCancelInvoiceRequest request) {
        invoiceService.approveInvoice(request);
    }

    @PostMapping("/cancel")
    @ResponseStatus(NO_CONTENT)
    public void cancelInvoice(@RequestBody @Valid ApproveAndCancelInvoiceRequest request) {
        invoiceService.cancelInvoice(request);
    }

    @PostMapping("/{invoiceId}/correction")
    @ResponseStatus(NO_CONTENT)
    public void requestCorrection(@PathVariable Long invoiceId, @RequestBody RequestCorrectionRequest request) {
        invoiceService.requestCorrection(invoiceId, request);
    }

    @GetMapping("/{invoiceId}")
    @ResponseStatus(OK)
    public InvoiceResponse findById(@PathVariable Long invoiceId) {
        return invoiceService.findById(invoiceId);
    }

    @GetMapping("/inbox/{recipientTaxId}")
    @ResponseStatus(OK)
    public List<InvoiceResponse> findAllInvoicesByRecipientUserTaxId(@PathVariable String recipientTaxId) {
        return invoiceService.findAllByRecipientUserTaxId(recipientTaxId);
    }


    @GetMapping("/outbox/{senderTaxId}")
    public List<InvoiceResponse> findInvoicesBySenderTaxId(@PathVariable("senderTaxId") String senderTaxId) {
        return invoiceService.findInvoicesBySenderTaxId(senderTaxId);
    }


    @PutMapping("/{recipientTaxId}/{invoiceId}")
    public InvoiceResponse updateInvoiceRecipientId(@PathVariable("recipientTaxId") String recipientTaxId,
                                                    @PathVariable("invoiceId") Long invoiceId) {
        return invoiceService.updateInvoiceRecipientId(recipientTaxId, invoiceId);
    }

    @PutMapping("/send-invoice")
    public List<InvoiceResponse> sendInvoice(@RequestBody SendInvoiceRequest request) {
        return invoiceService.sendInvoice(request);
    }

    @PutMapping("/correction")
    public InvoiceResponse sendInvoiceToCorrection(@RequestBody SendInvoiceToCorrectionRequest request) {
        return invoiceService.sendInvoiceToCorrection(request);
    }

    @PutMapping
    public InvoiceResponse updateInvoiceItems(@RequestBody UpdateInvoiceItemsRequest request) {
        return invoiceService.updateInvoiceItems(request);
    }


    @DeleteMapping
    public void deleteInvoiceById(@RequestBody DeleteInvoicesRequest request) {
        invoiceService.deleteInvoiceById(request);
    }


}