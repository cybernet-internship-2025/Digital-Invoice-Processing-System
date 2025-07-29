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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository mapper;
    private final MeasurementMapStruct mapStruct;

    @Override
    public MeasurementEntity getByName(String name) {
        return Optional.ofNullable(mapper.getByName(name))
                .orElseThrow(() -> new NotFoundException("Not_Found: " + name,"Measurement not found"));
    }

    @Override
    public MeasurementResponse getByNameResponse(String name) {
        MeasurementEntity entity = getByName(name);
        return mapStruct.toResponse(entity);
    }

    @Override
    public List<MeasurementResponse> findAll() {
        return mapStruct.toResponseList(mapper.findAll());
    }

    @Override
    public void updateMeasurement(Long id, MeasurementRequest request) {
        MeasurementEntity entity = mapper.getByName(request.getName());

        if (entity == null || !entity.getId().equals(id)) {
            throw new NotFoundException("Not_Found","Measurement not found");
        }

        entity.setName(request.getName());
        entity.setUpdateAt(LocalDateTime.now());
        mapper.updateMeasurement(entity);
    }

    @Override
    public void deleteMeasurement(Long id) {
        mapper.deleteMeasurement(id);
    }

    @Override
    public void restoreMeasurement(Long id) {
        mapper.restoreMeasurement(id);
    }
}


