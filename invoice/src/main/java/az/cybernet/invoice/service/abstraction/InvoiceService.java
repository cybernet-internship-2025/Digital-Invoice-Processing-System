package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.*;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
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

    InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId);

    List<InvoiceResponse> sendInvoice(SendInvoiceRequest request);

    InvoiceResponse sendInvoiceToCorrection(SendInvoiceToCorrectionRequest request);


    List<FilterResponse> findInvoicesBySenderTaxId(String senderTaxId, InvoiceFilterRequest filter);


    PaginatedInvoiceResponse findAllByRecipientUserTaxId(String recipientTaxId,
                                                      InvoiceFilterRequest filter,
                                                      Integer page,
                                                      Integer size);

    void approveInvoice(ApproveAndCancelInvoiceRequest request);

    void cancelInvoice(ApproveAndCancelInvoiceRequest request);

    void requestCorrection(Long invoiceId, RequestCorrectionRequest request);

    InvoiceResponse updateInvoiceItems(UpdateInvoiceItemsRequest request);

    byte[] exportExcelInvoice(String taxId, InvoiceFilterRequest invoiceFilterRequest );

}
