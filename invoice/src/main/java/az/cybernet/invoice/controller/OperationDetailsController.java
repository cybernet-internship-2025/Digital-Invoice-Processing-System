package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import az.cybernet.invoice.service.abstraction.OperationDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/operation-details")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OperationDetailsController {
    OperationDetailsService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public OperationDetailsResponse save(@RequestBody OperationDetailsEntity entity) {
        return service.save(entity);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(OK)
    public OperationDetailsResponse findByItemId(@PathVariable Long itemId) {
        return service.findByItemId(itemId);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<OperationDetailsResponse> findAll() {
        return service.findAll();
    }

}
