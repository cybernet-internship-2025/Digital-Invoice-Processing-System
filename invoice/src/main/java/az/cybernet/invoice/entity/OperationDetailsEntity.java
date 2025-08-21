package az.cybernet.invoice.entity;

import az.cybernet.invoice.enums.ItemStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE  )
public class OperationDetailsEntity {
    Long id;
    OperationEntity operation;
    ItemEntity item;
    ItemStatus itemStatus;
    Long operationId;
}
