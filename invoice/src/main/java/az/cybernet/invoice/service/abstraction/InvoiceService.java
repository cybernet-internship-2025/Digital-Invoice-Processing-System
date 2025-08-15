package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.request.invoice.PaginatedInvoiceResponse;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.ReturnInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceToCorrectionRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.entity.InvoiceEntity;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void deleteInvoiceById(Long invoiceId);

    InvoiceEntity fetchInvoiceIfExist(Long invoiceId);

    InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId);

    InvoiceResponse sendInvoice(SendInvoiceRequest request);

    InvoiceResponse sendInvoiceToCorrection(SendInvoiceToCorrectionRequest request);

    InvoiceResponse rollbackInvoice(Long invoiceId, String senderTaxId);

    PagedResponse<InvoiceResponse> findInvoicesBySenderTaxId(InvoiceFilterRequest filter);


    PaginatedInvoiceResponse findAllByRecipientUserTaxId(String recipientTaxId,
                                                         InvoiceFilterRequest filter,
                                                         Integer page,
                                                         Integer size);

    void approveInvoice(ApproveAndCancelInvoiceRequest request);

    void cancelInvoice(ApproveAndCancelInvoiceRequest request);

    void requestCorrection(Long invoiceId, RequestCorrectionRequest request);

    InvoiceResponse updateInvoiceItems(UpdateInvoiceItemsRequest request);

    InvoiceResponse createReturnInvoice(ReturnInvoiceRequest invoiceRequest, String currentUserTaxId);

    InvoiceResponse sendReturnInvoice(Long invoiceId, String senderTaxId, String recipientTaxId);

}
