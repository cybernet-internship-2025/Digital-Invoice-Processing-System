package az.cybernet.invoice.mapper;


import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.entity.OperationEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OperationMapStruct {
    OperationResponse toResponse(OperationEntity entity);
}