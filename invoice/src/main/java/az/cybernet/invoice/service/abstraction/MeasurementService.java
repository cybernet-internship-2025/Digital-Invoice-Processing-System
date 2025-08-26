package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;

import java.util.List;

public interface MeasurementService {
    void addMeasurement(MeasurementRequest request);

    List<MeasurementResponse> findAll();

    MeasurementResponse findByName(String name);

    void updateMeasurement(Long id, MeasurementRequest request);

    void deleteMeasurement(Long id);

    void restoreMeasurement(Long id);
}