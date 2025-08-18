package az.cybernet.invoice.controller.inner;

import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/invoices")
public class InvoiceControllerInternal {
    private final InvoiceService invoiceService;

    @GetMapping("/{invoiceId}")
    @ResponseStatus(HttpStatus.OK)
    public InvoiceResponse findById(@PathVariable @Positive Long invoiceId) {
        return invoiceService.findById(invoiceId);
    }
}
