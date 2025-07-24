package az.cybernet.invoice.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ItemsResponseDto {
    Long invoiceId;
    List<ItemResponseDto> itemsResponse;
}
