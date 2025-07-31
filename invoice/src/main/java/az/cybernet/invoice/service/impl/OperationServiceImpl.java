package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.entity.OperationEntity;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.mapper.OperationMapStruct;
import az.cybernet.invoice.repository.OperationRepository;
import az.cybernet.invoice.service.abstraction.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE,makeFinal = true)
public class OperationServiceImpl implements OperationService {

    OperationRepository operationRepository;
    OperationMapStruct operationMapStruct;


    @Override
    public void saveOperation(CreateOperationRequest request) {
        OperationEntity entity = operationMapStruct.toEntity(request);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setTaxId(request.getTaxId());
        System.out.println("Tax ID: " + request.getTaxId());

        System.out.println("Invoice ID:" + request.getInvoiceId());
        System.out.println("Item IDs:" + request.getItemIds());

        operationRepository.save(entity);
    }


    public List<Long> itemIds(CreateOperationRequest request) {
        List<Long> ids = new ArrayList<>();

        if (request.getInvoiceId() != null) {
            ids.add(request.getInvoiceId());
        }
        if (request.getItemIds() != null && !request.getItemIds().isEmpty()) {
            ids.addAll(request.getItemIds());
        }

        return ids;
    }


    @Override
    public List<OperationResponse> findAll() {
        return operationRepository.findAll()
                .stream()
                .map(operationMapStruct::toResponse)
                .collect(toList());
    }



    @Override
    public List<OperationResponse> findByStatus(OperationStatus status) {
        return operationRepository.findByStatus(status)
                .stream()
                .map(operationMapStruct::toResponse)
                .collect(toList());
    }

    @Override
    public List<OperationResponse> findAllItemsById(Long id) {
        return operationRepository.findAllItemsById(id)
                .stream()
                .map(operationMapStruct::toResponse)
                .collect(toList());
    }

    @Override
    public List<OperationResponse> findAllInvoicesById(Long id) {
        return operationRepository.findAllInvoicesById(id)
                .stream()
                .map(operationMapStruct::toResponse)
                .collect(toList());
    }


    @Transactional
    public OperationResponse changeStatus(Long operationId, OperationStatus newStatus, String comment) {
        OperationEntity op = operationRepository.findById(operationId)

                .orElseThrow(() -> new RuntimeException("Operation is not found: " + operationId));

        op.setStatus(newStatus);
        op.setComment(comment);

        operationRepository.save(op);

        return operationMapStruct.toResponse(op);
    }



    public OperationResponse  approve(Long id, String comment)    {
        return changeStatus(id, OperationStatus.APPROVED, comment); }

    public OperationResponse cancel(Long id, String comment)     {
        return changeStatus(id, OperationStatus.CANCELED, comment); }

    public OperationResponse draft(Long id, String comment)      {
        return changeStatus(id, OperationStatus.DRAFT, comment); }

    public OperationResponse correction(Long id, String comment) {
        return changeStatus(id, OperationStatus.CORRECTION, comment); }

    public OperationResponse pending(Long id, String comment)    {
        return changeStatus(id, OperationStatus.PENDING,comment);}
}