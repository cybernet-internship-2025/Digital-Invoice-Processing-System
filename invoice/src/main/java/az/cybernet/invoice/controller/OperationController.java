package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.service.abstraction.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/operation")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OperationController {
    OperationService operationService;

    @GetMapping
    public List<OperationResponse> findAll() {
        return operationService.findAll();
    }

    @GetMapping("/status")
    public List<OperationResponse> findByStatus(@RequestParam OperationStatus status) {
        return operationService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public List<OperationResponse> findAllItemsById(@PathVariable Long id) {
        return operationService.findAllItemsById(id);
    }

    @GetMapping("/{id}/invoice")
    public List<OperationResponse> findAllInvoicesById(@PathVariable Long id) {
        return operationService.findAllInvoicesById(id);
    }

    @PostMapping()
    public void saveOperation(@RequestBody CreateOperationRequest request) {
        operationService.saveOperation(request);
    }
}
