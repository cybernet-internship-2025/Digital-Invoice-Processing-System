package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.service.abstraction.OperationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }



    @GetMapping("/all")
    public List<OperationResponse> findAll(){
        return operationService.findAll();
    }

    @GetMapping("/status")
    public List<OperationResponse> findByStatus(@RequestParam OperationStatus status){
        return operationService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public List<OperationResponse> findAllItemsById(@PathVariable Long id){
        return operationService.findAllItemsById(id);
    }

    @GetMapping("/{id}/invoice")
    public List<OperationResponse> findAllInvoicesById (@PathVariable Long id){
        return operationService.findAllInvoicesById(id);
    }

    @PostMapping("/save")
    public void saveOperation(@RequestBody CreateOperationRequest request){
        operationService.saveOperation(request);}








}
