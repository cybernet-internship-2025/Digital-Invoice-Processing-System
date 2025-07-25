package az.cybernet.invoice.dto.request.operation;

//import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.enums.OperationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class CreateOperationRequest {
    OperationStatus status;
    String comment;
    Long invoiceId;
    Long itemId;
}
