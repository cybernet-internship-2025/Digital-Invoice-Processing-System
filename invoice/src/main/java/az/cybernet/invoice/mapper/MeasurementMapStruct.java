package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.entity.MeasurementEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MeasurementMapStruct {
    MeasurementResponse toResponse(MeasurementEntity entity);

    List<MeasurementResponse> toResponseList(List<MeasurementEntity> list);
}
