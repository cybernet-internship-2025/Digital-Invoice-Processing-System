package az.cybernet.invoice.dto.request.invoice;

import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class PaginatedInvoiceResponse {
    private List<InvoiceResponse> invoices;
    private boolean hasNext;
}
