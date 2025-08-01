package az.cybernet.invoice.dto.request.invoice;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CreateInvoiceRequest {
    @NotBlank(message = "Sender tax ID must not be blank")
    @Size(min = 10, max = 10, message = "Sender tax ID must be exactly 10 characters")
    String senderTaxId;

    @NotBlank(message = "Recipient tax ID must not be blank")
    @Size(min = 10, max = 10, message = "Recipient tax ID must be exactly 10 characters")
    String recipientTaxId;

    ItemsRequest items;
}
