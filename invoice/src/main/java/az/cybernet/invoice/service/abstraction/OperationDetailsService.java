package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import az.cybernet.invoice.repository.OperationDetailsRepository;

import java.util.List;

public interface OperationDetailsService {
    OperationDetailsResponse save(OperationDetailsEntity entity);
    OperationDetailsResponse findByItemId(Long itemId);
    List<OperationDetailsResponse> findAll();


}
