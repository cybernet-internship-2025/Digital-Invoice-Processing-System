package az.cybernet.invoice.repository;


import az.cybernet.invoice.entity.MeasurementEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeasurementRepository {

    MeasurementEntity getByName(String name);

    List<MeasurementEntity> findAll();

    void updateMeasurement(MeasurementEntity entity);

    void deleteMeasurement(Long id);

    void restoreMeasurement(Long id);
}

