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

    @Mapping(target = "name", source = "request.productName")
    @Mapping(target = "price", source = "request.unitPrice")
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "totalPrice", expression = "java(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())))")
    @Mapping(target = "measurement", source = "measurement")
    @Mapping(target = "invoice", source = "invoice")
    ItemEntity buildItemEntity(ItemRequest request, InvoiceEntity invoice, MeasurementEntity measurement);
}
