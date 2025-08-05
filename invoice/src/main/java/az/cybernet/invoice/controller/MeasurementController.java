package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.measurement.MeasurementRequest;
import az.cybernet.invoice.dto.response.measurement.MeasurementResponse;
import az.cybernet.invoice.service.abstraction.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;


    @PostMapping
    public ResponseEntity<Void> addMeasurement(@RequestBody MeasurementRequest request) {
        measurementService.addMeasurement(request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/by-name")
    public ResponseEntity<MeasurementResponse> getByName(@RequestParam String name) {
        return ResponseEntity.ok(measurementService.findByName(name));
    }


    @GetMapping
    public ResponseEntity<List<MeasurementResponse>> findAll() {
        return ResponseEntity.ok(measurementService.findAll());
    }



    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody MeasurementRequest request) {
        measurementService.updateMeasurement(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        measurementService.restoreMeasurement(id);
        return ResponseEntity.noContent().build();
    }
}
