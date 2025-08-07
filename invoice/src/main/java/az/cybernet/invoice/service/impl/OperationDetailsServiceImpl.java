package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import az.cybernet.invoice.mapper.OperationDetailsMapStruct;
import az.cybernet.invoice.repository.OperationDetailsRepository;
import az.cybernet.invoice.service.abstraction.OperationDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE,makeFinal = true)
public class OperationDetailsServiceImpl implements OperationDetailsService {

        OperationDetailsRepository operationDetailsRepository;
        OperationDetailsMapStruct operationDetailsMapStruct;


        @Override
        public OperationDetailsResponse save(OperationDetailsEntity entity) {
                operationDetailsRepository.save(entity);
                return operationDetailsMapStruct.toResponse(entity);
        }

        @Override
        public OperationDetailsResponse findByItemId(Long itemId) {
                OperationDetailsEntity entity = operationDetailsRepository.findByItemId(itemId);
                return operationDetailsMapStruct.toResponse(entity);
        }

        @Override
        public List<OperationDetailsResponse> findAll() {
                List<OperationDetailsEntity> entities = operationDetailsRepository.findAll();
                return operationDetailsMapStruct.toResponseList(entities);

        }

        @Override
        public OperationDetailsResponse update(OperationDetailsEntity entity) {
                operationDetailsRepository.update(entity);
                return operationDetailsMapStruct.toResponse(entity);
        }

        @Override
        public void delete(Long itemId) {
                operationDetailsRepository.delete(itemId);
        }
}
