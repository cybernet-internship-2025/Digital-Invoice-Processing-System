package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

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

    @PostMapping("/inbox/{recipientTaxId}")
    @ResponseStatus(OK)
    public PaginatedInvoiceResponse findAllInvoicesByRecipientUserTaxId(
            @PathVariable String recipientTaxId,
            @RequestBody InvoiceFilterRequest filter,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        return invoiceService.findAllByRecipientUserTaxId(recipientTaxId, filter, page, size);
}
    @GetMapping("/invoices/export/received")
    public void exportReceived(
            @RequestParam String recipientTaxId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response
    ) {
        var filter = InvoiceFilterRequest.builder()
                .status(status)
                .type(type)
                .invoiceNumber(invoiceNumber)
                .fromDate(fromDate)
                .toDate(toDate)
                .year(year)
                .build();

        invoiceService.exportReceivedInvoicesToExcel(recipientTaxId, filter, response);
    }

    @GetMapping("/outbox/{senderTaxId}")
    public PagedResponse<InvoiceResponse> findInvoicesBySenderTaxId(
            @PathVariable String senderTaxId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, name = "invoiceNumber") String invoiceNumber,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        InvoiceFilterRequest filter = new InvoiceFilterRequest();
        filter.setYear(year);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setStatus(status);
        filter.setType(type);
        filter.setInvoiceNumber(invoiceNumber);
        filter.setOffset(offset);
        filter.setLimit(limit);

        return invoiceService.findInvoicesBySenderTaxId(senderTaxId,filter);
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


    @PutMapping("/{id}/pending")
    public ResponseEntity<Void> markAsPending(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Səhvlər var") String comment) {
        invoiceService.markAsPending(id, comment);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/approve-timeout")
    public ResponseEntity<Void> approvePendingInvoicesAfterTimeout() {
        invoiceService.approvePendingInvoicesAfterTimeout();
        return ResponseEntity.ok().build();
    }


}