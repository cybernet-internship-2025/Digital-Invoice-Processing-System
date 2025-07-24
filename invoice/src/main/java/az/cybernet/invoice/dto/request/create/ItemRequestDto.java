package az.cybernet.invoice.dto.request.create;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    String productName;
    Integer unitPrice;
    Integer quantity;
    Integer totalPrice;
    String measurementName;
    Boolean isActive;
    LocalDateTime createdAt;

}
