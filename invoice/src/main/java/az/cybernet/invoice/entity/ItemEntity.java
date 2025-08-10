package az.cybernet.invoice.entity;

import az.cybernet.invoice.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ItemEntity {
    Long id;
    String name;
    BigDecimal unitPrice;
    Integer quantity;
    Boolean isActive;
    BigDecimal totalPrice;
    MeasurementEntity measurement;
    InvoiceEntity invoice;
    List<OperationEntity> operations;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    ItemStatus status;
}