package az.cybernet.invoice.dto.request.invoice;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class InvoiceFilterRequest {
    String status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime toDate;

    Integer year;
    String type;
    @Pattern(
            regexp = "^(INVD|INR)\\d{8}$",
            message = "Invoice number format must start with INVD or INR followed by 8 digits"
    )
    String invoiceNumber;
    Integer offset;
    Integer limit;
}