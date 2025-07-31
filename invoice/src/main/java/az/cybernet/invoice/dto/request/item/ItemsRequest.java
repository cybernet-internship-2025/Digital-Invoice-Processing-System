package az.cybernet.invoice.dto.request.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ItemsRequest {
    @JsonIgnore
    Long invoiceId;
    List<ItemRequest> itemsRequest;
}
