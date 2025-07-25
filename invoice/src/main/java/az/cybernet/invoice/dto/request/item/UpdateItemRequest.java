package az.cybernet.invoice.dto.request.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateItemRequest {
    Long id;
    String productName;
    BigDecimal totalPrice;
    BigDecimal unitPrice;
    Integer quantity;
    String measurementName;
    Long invoiceId;
}
