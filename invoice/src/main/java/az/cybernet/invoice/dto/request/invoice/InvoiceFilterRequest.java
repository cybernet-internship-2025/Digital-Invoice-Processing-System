package az.cybernet.invoice.dto.request.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class InvoiceFilterRequest {
    String status;
    LocalDateTime fromDate;
    LocalDateTime toDate;
    Integer year;
    String type;
    String series;
    String number;

    Integer offset;
    Integer limit;
}