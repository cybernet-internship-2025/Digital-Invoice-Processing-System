package az.cybernet.invoice.dto.request.operation;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddItemsToOperationRequest {
    private Long invoiceId;
    private String comment;
    private OperationStatus status;
    private List<Long> itemIds;
}
