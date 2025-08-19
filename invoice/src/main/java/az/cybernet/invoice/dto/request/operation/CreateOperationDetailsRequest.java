package az.cybernet.invoice.dto.request.operation;

import az.cybernet.invoice.enums.ItemStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CreateOperationDetailsRequest {
    Long itemId;
    Long operationId;
    ItemStatus itemStatus;
}
