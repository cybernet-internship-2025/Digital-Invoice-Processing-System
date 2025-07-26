package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    InvoiceResponse findById(Long id);

    void restoreInvoice(Long id);

    List<InvoiceResponse> getAll();

    List<InvoiceResponse> findAllByRecipientUserTaxId(String recipientTaxId);

    List<ItemResponse> addItemsToInvoice(ItemsRequest requests);

    void approveInvoice(Long invoiceId, String senderTaxId, String recipientTaxId);

    void cancelInvoice(Long invoiceId, String senderTaxId, String recipientTaxId);

    void requestCorrection(Long invoiceId, String senderTaxId, String recipientTaxId);

}
