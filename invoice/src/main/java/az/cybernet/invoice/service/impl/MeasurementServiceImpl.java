package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.exception.ExceptionConstants;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.MeasurementMapStruct;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.abstraction.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final MeasurementMapStruct mapStruct;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMeasurement(MeasurementRequest request) {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setName(request.getName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);

        measurementRepository.saveMeasurement(entity);
    }

    @Override
    public MeasurementResponse findByName(String name) {
        MeasurementEntity entity = measurementRepository.findByName(name);
        return mapStruct.toResponse(entity);
    }

    @Override
    public List<MeasurementResponse> findAll() {
        return mapStruct.toResponseList(measurementRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMeasurement(Long id, MeasurementRequest request) {
        MeasurementEntity entity = measurementRepository.findByName(request.getName()).orElseThrow(() ->
                new NotFoundException(
                        ExceptionConstants.MEASUREMENT_NOT_FOUND.getMessage(),
                        ExceptionConstants.MEASUREMENT_NOT_FOUND.getCode()
                )
        );

        entity.setName(request.getName());
        entity.setUpdatedAt(LocalDateTime.now());
        measurementRepository.updateMeasurement(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMeasurement(Long id) {
        measurementRepository.deleteMeasurement(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreMeasurement(Long id) {
        measurementRepository.restoreMeasurement(id);
    }
}