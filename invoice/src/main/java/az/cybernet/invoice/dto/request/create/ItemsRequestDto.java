package az.cybernet.invoice.dto.request.create;

import lombok.Data;

import java.util.List;

@Data
public class ItemsRequestDto {
    Long invoiceId;
    List<ItemRequestDto> itemsRequest;
}
