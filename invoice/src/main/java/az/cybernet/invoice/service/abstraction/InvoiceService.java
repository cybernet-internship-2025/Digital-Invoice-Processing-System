package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.SendInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void restoreInvoice(Long id);

    List<InvoiceResponse> getAll();

    void deleteInvoiceById(Long id);

    InvoiceResponse updateInvoiceRecipientId(String recipientTaxId, Long invoiceId);

    InvoiceResponse sendInvoice(Long invoiceId, SendInvoiceRequest request);

    List<InvoiceResponse> findAllByStatus(String status);

    InvoiceResponse sendInvoiceToCorrection(Long invoiceId, String senderTaxId);

    InvoiceResponse rollbackInvoice(Long invoiceId, String senderTaxId);

    List<InvoiceResponse> findInvoicesBySenderTaxId(Long senderTaxId);


    List<InvoiceResponse> findAllByRecipientUserTaxId(String recipientTaxId);

    void approveInvoice(Long invoiceId, String senderTaxId, String recipientTaxId);

    void cancelInvoice(Long invoiceId, String senderTaxId, String recipientTaxId);

    void requestCorrection(Long invoiceId, String senderTaxId, String recipientTaxId, String comment);

}
