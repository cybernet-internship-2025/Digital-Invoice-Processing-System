package az.cybernet.invoice.dto.response.operation;

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
public class OperationDetailsResponse {
    Long id;
    Long operationId;
    Long itemId;
    ItemStatus itemStatus;
}
