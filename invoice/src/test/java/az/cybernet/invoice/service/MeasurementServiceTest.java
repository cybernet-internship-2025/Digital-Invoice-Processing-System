package az.cybernet.invoice.service;


import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.mapper.MeasurementMapStruct;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.impl.MeasurementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @InjectMocks
    private MeasurementServiceImpl measurementService;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private MeasurementMapStruct mapStruct;

    @Test
    void addMeasurementTest() {
        MeasurementRequest request = new MeasurementRequest();
        request.setName("kg");

        MeasurementEntity entity = new MeasurementEntity();
        entity.setName("kg");

        doNothing().when(measurementRepository).saveMeasurement(entity);

        measurementService.addMeasurement(request);

        verify(measurementRepository, times(1)).saveMeasurement(entity);
    }

    @Test
    void updateMeasurementTest() {
        Long id = 1L;
        MeasurementRequest request = new MeasurementRequest();
        request.setName("kg");

        MeasurementEntity entity = new MeasurementEntity();
        entity.setId(id);
        entity.setName("kg");

        when(measurementRepository.getByName(request.getName())).thenReturn(entity);
        doNothing().when(measurementRepository).updateMeasurement(entity);

        measurementService.updateMeasurement(id, request);

        verify(measurementRepository, times(1)).updateMeasurement(entity);
        assertEquals("kg", entity.getName());
    }

    @Test
    void findByNameTest() {

        String name = "kg";

        MeasurementEntity entity = new MeasurementEntity();
        entity.setId(1L);
        entity.setName(name);

        MeasurementResponse expectedResponse = new MeasurementResponse();
        expectedResponse.setId(1L);
        expectedResponse.setName(name);

        when(measurementRepository.getByName(name)).thenReturn(entity);
        when(mapStruct.toResponse(entity)).thenReturn(expectedResponse);


        MeasurementResponse actualResponse = measurementService.findByName(name);


        verify(measurementRepository, times(1)).getByName(name);
        verify(mapStruct, times(1)).toResponse(entity);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
    }

    @Test
    void updateMeasurementTestt() {
        Long id = 1L;
        MeasurementRequest request = new MeasurementRequest();
        request.setName("kg");


        when(measurementRepository.getByName(request.getName())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> measurementService.updateMeasurement(id, request));
    }

    @Test
    void deleteMeasurementTest() {
        Long id = 1L;

        doNothing().when(measurementRepository).deleteMeasurement(id);

        measurementService.deleteMeasurement(id);

        verify(measurementRepository, times(1)).deleteMeasurement(id);
    }

    @Test
    void restoreMeasurementTest() {

        Long measurementId = 123L;

        measurementService.restoreMeasurement(measurementId);

        verify(measurementRepository, times(1)).restoreMeasurement(measurementId);
    }
}