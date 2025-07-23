package az.cybernet.invoice.entity;

import az.cybernet.invoice.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class OperationEntity {
    Long id;
    OperationStatus status;
    String comment;
    LocalDateTime createdAt;
    InvoiceEntity invoice;
    ItemEntity item;
}
