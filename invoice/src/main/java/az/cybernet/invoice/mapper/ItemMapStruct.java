package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.ItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapStruct {
    ItemResponse toResponse(ItemEntity item);

    List<ItemResponse> toResponseList(List<ItemEntity> items);

    ItemEntity toEntity(ItemRequest itemRequest);
}
