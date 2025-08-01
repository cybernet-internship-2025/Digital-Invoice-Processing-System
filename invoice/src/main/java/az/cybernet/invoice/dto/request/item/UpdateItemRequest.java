package az.cybernet.invoice.dto.request.item;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateItemRequest {
    @NotNull(message = "Item ID cannot be null")
    Long id;
    String name;
    BigDecimal unitPrice;
    Integer quantity;
    String measurementName;
}