package az.cybernet.invoice.dto.request.create;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    String productName;
    BigDecimal unitPrice;
    Integer quantity;
    Integer totalPrice;
    String measurementName;

}
