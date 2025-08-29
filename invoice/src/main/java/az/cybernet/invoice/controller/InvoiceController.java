package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.service.abstraction.InvoiceFileService;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.util.ExcelFileExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    ExcelFileExporter excelFileExporter;
    InvoiceFileService invoiceFileService;

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

    @GetMapping("/export/received")
    @ResponseStatus(NO_CONTENT)
    public void exportReceived(@RequestBody InvoiceExportRequest request,
                               HttpServletResponse response) {
        invoiceService.exportReceivedInvoicesToExcel(request, response);
    }

    @GetMapping("/outbox/{senderTaxId}")
    public List<FilterResponse> findInvoicesBySenderTaxId(@RequestParam String senderTaxId,
                                                          @RequestBody InvoiceFilterRequest request) {
        return invoiceService.findInvoicesBySenderTaxId(senderTaxId, request);
    }



    @PutMapping("/update-recipient")
    public InvoiceResponse updateInvoiceRecipientId(@RequestBody UpdateInvoiceRecipientTaxIdRequest request) {
        return invoiceService.updateInvoiceRecipientTaxId(request);
    }

    @PutMapping("/send-invoice")
    public List<InvoiceResponse> sendInvoice(@RequestBody SendInvoiceRequest request) {
        return invoiceService.sendInvoice(request);
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

    @PutMapping("/send-cancel/{invoiceId}/{receiverTaxId}")
    void sendInvoiceToCancel(@PathVariable("invoiceId") Long invoiceId,
                             @PathVariable("receiverTaxId") String receiverTaxId) {
        invoiceService.sendInvoiceToCancel(invoiceId, receiverTaxId);
    }

    @PutMapping("/cancel-timeout")
    void cancelPendingInvoicesAfterTimeout() {
        invoiceService.cancelPendingInvoicesAfterTimeout();
    }

    @PostMapping("/{taxId}/sent/export-to-excelexport")
    public ResponseEntity<byte[]> exportSentInvoiceToExcel(
            @PathVariable("taxId") String taxId,
            @RequestBody @Valid InvoiceFilterRequest invoiceFilterRequest,
            @RequestParam(value = "fileName", defaultValue = "Invoice") String fileName) {
        return excelFileExporter.buildExcelResponse(
                invoiceFileService.exportInvoiceToExcel(invoiceFilterRequest,taxId),
                fileName
        );
    }



}