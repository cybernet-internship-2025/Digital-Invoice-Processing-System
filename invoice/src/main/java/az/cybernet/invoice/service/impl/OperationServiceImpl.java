package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequestDetails;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.OperationEntity;
import az.cybernet.invoice.entity.OperationEntityDetails;
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
    @Transactional
    public void saveOperation(CreateOperationRequest request) {
        OperationEntity entity = new OperationEntity();
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus(request.getStatus());
        entity.setComment(request.getComment());

        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setId(request.getInvoiceId());
        entity.setInvoice(invoiceEntity);


        List<OperationEntityDetails> detailsList = new ArrayList<>();

        for (CreateOperationRequestDetails detail : request.getItems()) {
            OperationEntityDetails operationDetail = OperationEntityDetails.builder()
                    .item(ItemEntity.builder().id(detail.getItemId()).build())
                    .itemStatus(detail.getItemStatus())
                    .comment(detail.getComment())
                    .operation(entity)
                    .build();

            detailsList.add(operationDetail);
        }

        entity.setItemDetails(detailsList);

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



    @Transactional
    public OperationResponse  approve(Long id, String comment)    {
        return changeStatus(id, OperationStatus.APPROVED, comment); }

    @Transactional
    public OperationResponse cancel(Long id, String comment)     {
        return changeStatus(id, OperationStatus.CANCELED, comment); }

    @Transactional
    public OperationResponse draft(Long id, String comment)      {
        return changeStatus(id, OperationStatus.DRAFT, comment); }

    @Transactional
    public OperationResponse correction(Long id, String comment) {
        return changeStatus(id, OperationStatus.CORRECTION, comment); }

    @Transactional
    public OperationResponse pending(Long id, String comment)    {
        return changeStatus(id, OperationStatus.PENDING,comment);}

    @Transactional
    public OperationResponse deleted(Long id, String comment)   {
        return changeStatus(id, OperationStatus.DELETE,comment);}
    @Transactional
    public OperationResponse update(Long id, String comment)   {
        return changeStatus(id, OperationStatus.UPDATE,comment);}
}