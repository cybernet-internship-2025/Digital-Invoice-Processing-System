package az.cybernet.invoice.dto.response.operation;

import az.cybernet.invoice.enums.ItemStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDetailsResponse {
    Long id;
    Long operationId;
    Long itemId;
    ItemStatus itemStatus;
}
