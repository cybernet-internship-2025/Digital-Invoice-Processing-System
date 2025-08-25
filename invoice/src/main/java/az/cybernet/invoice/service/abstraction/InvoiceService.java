package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.invoice.PagedResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void deleteInvoiceById(DeleteInvoicesRequest request);

    InvoiceEntity fetchInvoiceIfExist(Long invoiceId);

    InvoiceResponse updateInvoiceRecipientTaxId(UpdateInvoiceRecipientTaxIdRequest request);

    List<InvoiceResponse> sendInvoice(SendInvoiceRequest request);


    PagedResponse<InvoiceResponse> findInvoicesBySenderTaxId(String senderTaxId, InvoiceFilterRequest filter);


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

    void markAsPending(Long invoiceId, String comment);

    void approvePendingInvoicesAfterTimeout();

    void exportReceivedInvoicesToExcel(
            InvoiceExportRequest request,
            HttpServletResponse response
    );

    void sendInvoiceToCancel(Long invoiceId, String receiverTaxId);

    void cancelPendingInvoicesAfterTimeout();

}
