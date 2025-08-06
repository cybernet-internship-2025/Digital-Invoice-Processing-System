package az.cybernet.invoice.entity;

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
    OperationEntity operation;
    ItemEntity item;
    ItemStatus itemStatus;
}
