package az.cybernet.invoice.entity;

import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.enums.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
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
