package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/{invoiceId}/approve")
    @ResponseStatus(NO_CONTENT)
    public void approveInvoice(@PathVariable Long invoiceId, @RequestBody @Valid ApproveAndCancelInvoiceRequest request) {
        invoiceService.approveInvoice(invoiceId, request);
    }

    @PostMapping("/{invoiceId}/cancel")
    @ResponseStatus(NO_CONTENT)
    public void cancelInvoice(@PathVariable Long invoiceId, @RequestBody @Valid ApproveAndCancelInvoiceRequest request) {
        invoiceService.cancelInvoice(invoiceId, request);
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

    @PostMapping("/{invoiceId}/restore")
    @ResponseStatus(NO_CONTENT)
    public void restoreInvoice(@PathVariable Long invoiceId) {
        invoiceService.restoreInvoice(invoiceId);
    }

    @GetMapping("/inbox/{recipientTaxId}")
    @ResponseStatus(OK)
    public List<InvoiceResponse> findAllInvoicesByRecipientUserTaxId(@PathVariable String recipientTaxId) {
        return invoiceService.findAllByRecipientUserTaxId(recipientTaxId);
    }


    @GetMapping("/outbox/{senderTaxId}")
    public List<InvoiceResponse> findInvoicesBySenderTaxId(@PathVariable String senderTaxId,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(required = false) Integer year,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                           @RequestParam(required = false) String invoiceNumber,
                                                           @RequestParam(defaultValue = "0") Integer offset,
                                                           @RequestParam(defaultValue = "10") Integer limit) {
        FilterInvoiceRequest filter = new FilterInvoiceRequest();
        filter.setStatus(status);
        filter.setYear(year);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setInvoiceNumber(invoiceNumber);
        filter.setOffset(offset);
        filter.setLimit(limit);

        return invoiceService.findInvoicesBySenderTaxId(senderTaxId, filter);
    }


    @PutMapping("/{recipientTaxId}/{invoiceId}")
    public InvoiceResponse updateInvoiceRecipientId(@PathVariable("recipientTaxId") String recipientTaxId,
                                                    @PathVariable("invoiceId") Long invoiceId) {
        return invoiceService.updateInvoiceRecipientId(recipientTaxId, invoiceId);
    }

    @PutMapping("/send-invoice")
    public InvoiceResponse sendInvoice(@RequestBody SendInvoiceRequest request) {
        return invoiceService.sendInvoice(request);
    }

    @PutMapping("/correction")
    public InvoiceResponse sendInvoiceToCorrection(@RequestBody SendInvoiceToCorrectionRequest request) {
        return invoiceService.sendInvoiceToCorrection(request);
    }

    @PutMapping("/rollback/{invoiceId}/{senderTaxId}")
    public InvoiceResponse rollbackInvoice(@PathVariable("invoiceId") Long invoiceId,
                                           @PathVariable("senderTaxId") String senderTaxId) {
        return invoiceService.rollbackInvoice(invoiceId, senderTaxId);
    }

    @PutMapping
    public InvoiceResponse updateInvoiceItems(@RequestBody UpdateInvoiceItemsRequest request){
        return invoiceService.updateInvoiceItems(request);
    }


    @DeleteMapping("/{invoiceId}")
    public void deleteInvoiceById(@PathVariable("invoiceId") Long invoiceId) {
        invoiceService.deleteInvoiceById(invoiceId);
    }


}