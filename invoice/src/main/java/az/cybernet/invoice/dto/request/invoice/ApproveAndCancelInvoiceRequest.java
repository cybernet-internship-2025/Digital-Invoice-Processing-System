package az.cybernet.invoice.dto.request.invoice;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ApproveAndCancelInvoiceRequest {
    @NotBlank String senderTaxId;
    @NotBlank String recipientTaxId;
}
