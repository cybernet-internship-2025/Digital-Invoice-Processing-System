package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.service.abstraction.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
public class MeasurementController {
    private final MeasurementService measurementService;

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