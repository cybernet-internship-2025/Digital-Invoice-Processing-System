package az.cybernet.invoice.dto.response.item;

import lombok.Data;
import java.util.List;

@Data
public class ItemsResponse {
    Long invoiceId;
    List<ItemResponse> itemsResponse;
}
