package az.cybernet.invoice.dto.response.item;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemResponseDto {
    Long id;
    String productName;
    BigDecimal totalPrice;
    BigDecimal unitPrice;
    Integer quantity;
    Boolean isActive;
    Long invoiceId;
    String measurementName;
}

