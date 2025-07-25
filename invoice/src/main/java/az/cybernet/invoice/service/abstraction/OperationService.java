package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.enums.OperationStatus;

import java.util.List;

public interface OperationService {


     void saveOperation(CreateOperationRequest createOperationRequest);

    List<OperationResponse> findAll();
    List<OperationResponse> findByStatus(OperationStatus status);
    List<OperationResponse> findAllItemsById(Long id);
    List<OperationResponse> findAllInvoicesById(Long id);

    //
}
