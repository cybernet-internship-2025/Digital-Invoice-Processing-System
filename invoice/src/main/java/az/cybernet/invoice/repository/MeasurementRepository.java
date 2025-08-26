package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.MeasurementEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MeasurementRepository {
    void saveMeasurement(MeasurementEntity entity);

    Optional<List<MeasurementEntity>> findAll();

    Optional<MeasurementEntity> findByName(String name);

    Optional<MeasurementEntity> findById(Long id);

    void updateMeasurement(MeasurementEntity entity);

    void deleteMeasurement(Long id);

    void restoreMeasurement(Long id);
}