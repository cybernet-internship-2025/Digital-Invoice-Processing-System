package az.cybernet.invoice.mapper;


import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapStruct {

    ItemResponse toResponse(ItemEntity item);
    List<ItemResponse> toResponseList(List<ItemEntity> items);

    @Mapping(source = "name", target = "productName")
    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "measurement.name", target = "measurementName")
    ItemResponse map(ItemEntity entity);

    List<ItemResponse> map(List<ItemEntity> entities);
}
