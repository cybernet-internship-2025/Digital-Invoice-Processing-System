package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.UpdateInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse saveInvoice(CreateInvoiceRequest invoiceRequest);

    List<ItemResponse> addItemsToInvoice(ItemsRequest request);

    List<InvoiceResponse> getAll();

    void deleteInvoiceById(Long id);

    InvoiceResponse updateInvoice(UpdateInvoiceRequest request, Long invoiceId);

}
