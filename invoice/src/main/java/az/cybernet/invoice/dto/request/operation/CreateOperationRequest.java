package az.cybernet.invoice.dto.request.operation;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    Long invoiceId;
    List<CreateOperationDetailsRequest> items;
    String comment;
}

