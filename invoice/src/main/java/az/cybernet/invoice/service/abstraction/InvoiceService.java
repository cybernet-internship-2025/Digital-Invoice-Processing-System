package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void deleteInvoiceById(DeleteInvoicesRequest request);

    InvoiceEntity fetchInvoiceIfExist(Long invoiceId);

    InvoiceResponse updateInvoiceRecipientTaxId(UpdateInvoiceRecipientTaxIdRequest request);

    List<InvoiceResponse> sendInvoice(SendInvoiceRequest request);


    List<FilterResponse> findInvoicesBySenderTaxId(String senderTaxId, InvoiceFilterRequest filter);


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

    void processReturnInvoice(ReturnInvoiceRequest request,
                              Long  invoiceReturnId,
                              InvoiceStatus status,
                              String comment);

    void approveReturnInvoice(Long returnInvoiceId, ReturnInvoiceRequest request);

    void cancelReturnInvoice(Long returnInvoiceId, ReturnInvoiceRequest request);

    void requestReturnCorrection(Long returnInvoiceId, ReturnInvoiceRequest request);

    void markAsPending(Long invoiceId, String comment);

    void approvePendingInvoicesAfterTimeout();

    void sendInvoiceToCancel(Long invoiceId, String receiverTaxId);

    void cancelPendingInvoicesAfterTimeout();

}
