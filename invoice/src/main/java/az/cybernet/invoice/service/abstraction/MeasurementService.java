package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.entity.MeasurementEntity;

import java.util.List;

public interface MeasurementService {

    void addMeasurement(MeasurementRequest request);

    List<MeasurementResponse> findAll();

    void updateMeasurement(Long id, MeasurementRequest request);

    void deleteMeasurement(Long id);

    void restoreMeasurement(Long id);

    MeasurementResponse findByName(String name);

}

