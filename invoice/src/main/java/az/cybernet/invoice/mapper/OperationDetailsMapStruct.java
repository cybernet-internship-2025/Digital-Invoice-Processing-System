package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationDetailsMapStruct {

    @Mapping(source = "operation.id", target = "operationId")
    @Mapping(source = "item.id", target = "itemId")
    OperationDetailsResponse toResponse(OperationDetailsEntity entity);
    List<OperationDetailsResponse> toResponseList(List<OperationDetailsEntity> entityList);
}
