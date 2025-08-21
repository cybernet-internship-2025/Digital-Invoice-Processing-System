package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import az.cybernet.invoice.util.ExcelFileExporter;
import feign.Param;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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


    @GetMapping("/outbox/{senderTaxId}")
    @ResponseStatus(OK)
    public List<FilterResponse> findInvoicesBySenderTaxId(@PathVariable String senderTaxId,
                                                          @RequestBody InvoiceFilterRequest filter){
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
    @PostMapping("/{taxId}/sent/export-to-excel")
    public ResponseEntity<byte[]> exportSentInvoiceToExcel(
            @PathVariable("taxId") String taxId,
            @RequestBody @Valid InvoiceFilterRequest invoiceFilterRequest,
            @RequestParam(value = "fileName", defaultValue = "Invoice") String fileName) {
        return excelFileExporter.buildExcelResponse(
                invoiceService.exportExcelInvoice(taxId, invoiceFilterRequest),
                fileName
        );
    }


}