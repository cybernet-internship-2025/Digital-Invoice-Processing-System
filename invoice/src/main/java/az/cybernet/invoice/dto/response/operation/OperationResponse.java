package az.cybernet.invoice.dto.response.operation;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class OperationResponse {
    Long id;
    OperationStatus status;
    String comment;
    LocalDateTime createdAt;
    Long invoiceId;
    Long itemId;
}
