package az.cybernet.invoice.dto.request.operation;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CreateOperationRequest {
    OperationStatus status;
    String comment;
    Long invoiceId;
    List<Long> itemIds;
    List<CreateOperationDetailsRequest> items;
}

