package az.cybernet.invoice.dto.request.item;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemsRequest {
    @NotNull(message = "Invoice ID cannot be null")
    Long invoiceId;
    List<ItemRequest> itemsRequest;
}
