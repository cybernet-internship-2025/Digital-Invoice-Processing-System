package az.cybernet.filestorage.dto.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {
    Long id;
    String senderTaxId;
    String recipientTaxId;
    BigDecimal totalPrice;
    LocalDateTime createdAt;
    String invoiceNumber;
    String invoiceSeries;
    List<ItemResponse> items;
}
