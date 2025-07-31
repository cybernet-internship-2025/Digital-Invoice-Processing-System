package az.cybernet.invoice.dto.request.operation;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = PRIVATE)
public class CreateOperationRequest {
    OperationStatus status;
    String comment;
    Long invoiceId;
    String taxId;
    List<Long> itemIds;




}
