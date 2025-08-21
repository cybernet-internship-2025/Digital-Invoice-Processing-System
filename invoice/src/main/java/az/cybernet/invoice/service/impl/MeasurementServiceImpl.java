package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.MeasurementMapStruct;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.abstraction.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static az.cybernet.invoice.exception.ExceptionConstants.MEASUREMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {
    private final MeasurementRepository mapper;
    private final MeasurementMapStruct mapStruct;

    @Override
    public void addMeasurement(MeasurementRequest request) {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setName(request.getName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);

        mapper.saveMeasurement(entity);
    }

    @Override
    public List<MeasurementResponse> findAll() {
        return mapStruct.toResponseList(mapper.findAll().orElseThrow(() ->
                new NotFoundException(
                        MEASUREMENT_NOT_FOUND.getCode(),
                        MEASUREMENT_NOT_FOUND.getMessage())));
    }

    @Override
    public MeasurementResponse findByName(String name) {
        MeasurementEntity entity = mapper.findByName(name).orElseThrow(() ->
                new NotFoundException(
                        MEASUREMENT_NOT_FOUND.getCode(),
                        MEASUREMENT_NOT_FOUND.getMessage()));
        return mapStruct.toResponse(entity);
    }

    private MeasurementEntity findById(Long id) {
        return mapper.findById(id).orElseThrow(
                () -> new NotFoundException(
                        MEASUREMENT_NOT_FOUND.getCode(),
                        MEASUREMENT_NOT_FOUND.getMessage()
                ));
    }

    @Override
    public void updateMeasurement(Long id, MeasurementRequest request) {
        MeasurementEntity entity = findById(id);

        entity.setName(request.getName());
        entity.setUpdatedAt(LocalDateTime.now());

        mapper.updateMeasurement(entity);
    }

    @Override
    public void deleteMeasurement(Long id) {
        findById(id);
        mapper.deleteMeasurement(id);
    }

    @Override
    public void restoreMeasurement(Long id) {
        findById(id);
        mapper.restoreMeasurement(id);
    }
}