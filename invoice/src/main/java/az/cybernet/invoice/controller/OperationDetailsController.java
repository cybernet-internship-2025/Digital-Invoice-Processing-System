package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import az.cybernet.invoice.service.abstraction.OperationDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation-details")
@RequiredArgsConstructor
public class OperationDetailsController {

    private final OperationDetailsService service;

    @PostMapping
    public OperationDetailsResponse save(@RequestBody OperationDetailsEntity entity) {
        return service.save(entity);
    }

    @GetMapping("/{itemId}")
    public OperationDetailsResponse findByItemId(@PathVariable Long itemId) {
        return service.findByItemId(itemId);
    }

    @GetMapping
    public List<OperationDetailsResponse> findAll() {
        return service.findAll();
    }

   
}
