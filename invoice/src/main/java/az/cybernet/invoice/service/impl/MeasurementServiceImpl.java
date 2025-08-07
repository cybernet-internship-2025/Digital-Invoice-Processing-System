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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static az.cybernet.invoice.exception.ExceptionConstants.MEASUREMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository mapper;
    private final MeasurementMapStruct mapStruct;

    @Override
    @Transactional
    public void addMeasurement(MeasurementRequest request) {
        MeasurementEntity entity = new MeasurementEntity();
        entity.setName(request.getName());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsActive(true);

        mapper.saveMeasurement(entity);
    }

    @Override
    public MeasurementResponse findByName(String name) {
        MeasurementEntity entity = mapper.findByName(name);
        return mapStruct.toResponse(entity);
    }


    @Override
    public List<MeasurementResponse> findAll() {
        return mapStruct.toResponseList(mapper.findAll());
    }

    @Override
    @Transactional
    public void updateMeasurement(Long id, MeasurementRequest request) {
        MeasurementEntity entity = mapper.findByName(request.getName());

        if (entity == null || !entity.getId().equals(id)) {
            throw new NotFoundException(MEASUREMENT_NOT_FOUND.getCode(), MEASUREMENT_NOT_FOUND.getMessage());
        }

        entity.setName(request.getName());
        entity.setUpdateAt(LocalDateTime.now());
        mapper.updateMeasurement(entity);
    }

    @Override
    @Transactional
    public void deleteMeasurement(Long id) {
        mapper.deleteMeasurement(id);
    }

    @Override
    @Transactional
    public void restoreMeasurement(Long id) {
        mapper.restoreMeasurement(id);
    }
}