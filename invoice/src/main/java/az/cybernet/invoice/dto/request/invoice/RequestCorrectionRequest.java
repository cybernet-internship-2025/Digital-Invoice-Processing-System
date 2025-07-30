package az.cybernet.invoice.dto.request.invoice;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public record RequestCorrectionRequest(
        @NotBlank String senderTaxId,
        @NotBlank String recipientTaxId,
        String comment
) {
}
