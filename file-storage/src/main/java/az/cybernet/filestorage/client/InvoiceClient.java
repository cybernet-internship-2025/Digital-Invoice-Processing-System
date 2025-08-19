package az.cybernet.filestorage.client;

import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "invoice-service", url = "${invoice.service.url}")
public interface InvoiceClient {
    @GetMapping("/api/internal/invoices/{invoiceId}")
    InvoiceResponse findInvoiceById(@PathVariable("invoiceId") Long invoiceId);
}
