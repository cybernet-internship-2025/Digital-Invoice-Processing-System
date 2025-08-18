package az.cybernet.filestorage.dto.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponse {
    Long id;
    String productName;
    BigDecimal unitPrice;
    Integer quantity;
    BigDecimal totalPrice;
    Boolean isActive;
    Long invoiceId;
    String measurementName;
}
