package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void restoreInvoice(Long id);


    void deleteInvoiceById(Long invoiceId);

    InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId);

    InvoiceResponse sendInvoice(SendInvoiceRequest request);

    InvoiceResponse sendInvoiceToCorrection(SendInvoiceToCorrectionRequest request);

    InvoiceResponse rollbackInvoice(Long invoiceId, String senderTaxId);

    List<InvoiceResponse> findInvoicesBySenderTaxId(String senderTaxId);


    List<InvoiceResponse> findAllByRecipientUserTaxId(String recipientTaxId);

    void approveInvoice(Long invoiceId, ApproveAndCancelInvoiceRequest request);

    void cancelInvoice(Long invoiceId, ApproveAndCancelInvoiceRequest request);

    void requestCorrection(Long invoiceId, RequestCorrectionRequest request);

    InvoiceResponse updateInvoiceItems(UpdateInvoiceItemsRequest request);

}
