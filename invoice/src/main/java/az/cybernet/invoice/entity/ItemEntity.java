package az.cybernet.invoice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ItemEntity {
    Long id;
    String name;
    BigDecimal price;
    Integer quantity;
    Boolean isActive;
    BigDecimal totalPrice;
    MeasurementEntity measurement;
    InvoiceEntity invoice;
    List<OperationEntity> operations;
}
