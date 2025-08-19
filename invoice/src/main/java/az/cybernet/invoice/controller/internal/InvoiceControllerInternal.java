package az.cybernet.invoice.controller.internal;

import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.service.abstraction.InvoiceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/invoices")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InvoiceControllerInternal {
    InvoiceService invoiceService;

    @GetMapping("/{invoiceId}")
    @ResponseStatus(HttpStatus.OK)
    public InvoiceResponse findById(@PathVariable @Positive Long invoiceId) {
        return invoiceService.findById(invoiceId);
    }
}
