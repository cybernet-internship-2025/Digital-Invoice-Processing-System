package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.service.abstraction.MeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MeasurementController {
    MeasurementService measurementService;

    @PostMapping
    public ResponseEntity<Void> addMeasurement(@RequestBody MeasurementRequest request) {
        measurementService.addMeasurement(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MeasurementResponse>> findAll() {
        return ResponseEntity.ok(measurementService.findAll());
    }

    @GetMapping("/{measurementName}")
    public ResponseEntity<MeasurementResponse> findByName(@PathVariable String measurementName) {
        return ResponseEntity.ok(measurementService.findByName(measurementName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody MeasurementRequest request) {
        measurementService.updateMeasurement(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
    }

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable Long id) {
        measurementService.restoreMeasurement(id);
    }
}