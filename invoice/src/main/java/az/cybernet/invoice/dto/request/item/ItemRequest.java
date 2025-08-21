package az.cybernet.invoice.dto.request.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    @NotBlank(message = "Item name cannot be blank")
    String productName;

    @NotNull(message = "Unit price cannot be null")
    BigDecimal unitPrice;

    @NotNull(message = "Quantity cannot be null")
    Integer quantity;

    @NotBlank(message = "Measurement name cannot be blank")
    String measurementName;
}
