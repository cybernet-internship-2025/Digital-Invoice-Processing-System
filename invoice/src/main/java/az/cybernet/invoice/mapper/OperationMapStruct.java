package az.cybernet.invoice.mapper;


import az.cybernet.invoice.dto.request.create.CreateOperationRequest;
import az.cybernet.invoice.dto.response.OperationResponse;
import az.cybernet.invoice.entity.OperationEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OperationMapStruct {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    OperationEntity toEntity(CreateOperationRequest request);
    OperationResponse toResponse(OperationEntity entity);
}