package az.cybernet.invoice.dto.response.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
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

