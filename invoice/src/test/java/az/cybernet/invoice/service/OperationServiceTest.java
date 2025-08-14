package az.cybernet.invoice.service;

import az.cybernet.invoice.dto.request.operation.CreateOperationDetailsRequest;
import az.cybernet.invoice.dto.request.operation.CreateOperationRequest;
import az.cybernet.invoice.dto.response.operation.OperationResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.OperationEntity;
import az.cybernet.invoice.enums.ItemStatus;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.mapper.OperationMapStruct;
import az.cybernet.invoice.repository.OperationRepository;
import az.cybernet.invoice.service.abstraction.OperationDetailsService;
import az.cybernet.invoice.service.impl.OperationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {
    @InjectMocks
    private OperationServiceImpl operationService;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationMapStruct operationMapStruct;
    @Mock
    private OperationDetailsService operationDetailsService;



    @Test
    void saveOperation_testing_withitems() {
        CreateOperationDetailsRequest detail = CreateOperationDetailsRequest.builder()
                .itemId(1L)
                .itemStatus(ItemStatus.CREATED)
                .build();

        CreateOperationRequest request = CreateOperationRequest.builder()
                .status(OperationStatus.PENDING)
                .invoiceId(10L)
                .items(List.of(detail))
                .comment("Test Comment")
                .build();

        operationService.saveOperation(request);

        verify(operationRepository, times(1)).save(any(OperationEntity.class));
    }

    @Test
    void findAll_ShouldReturnMappedResponses() {
        OperationEntity entity = OperationEntity.builder()
                .id(1L)
                .status(OperationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .invoice(InvoiceEntity.builder().id(10L).build())
                .comment("Test")
                .build();

        OperationResponse response = new OperationResponse(); // Varsayalım boş DTO
        when(operationRepository.findAll()).thenReturn(List.of(entity));
        when(operationMapStruct.toResponse(entity)).thenReturn(response);

        List<OperationResponse> result = operationService.findAll();

        assertThat(result).hasSize(1).containsExactly(response);
    }

    @Test
    void findByStatus_ShouldFilterByStatus() {
        OperationEntity entity = OperationEntity.builder()
                .id(2L)
                .status(OperationStatus.APPROVED)
                .build();

        OperationResponse response = new OperationResponse();
        when(operationRepository.findByStatus(OperationStatus.APPROVED)).thenReturn(List.of(entity));
        when(operationMapStruct.toResponse(entity)).thenReturn(response);

        List<OperationResponse> result = operationService.findByStatus(OperationStatus.APPROVED);

        assertThat(result).hasSize(1).containsExactly(response);
    }

    @Test
    void changeStatus_ShouldUpdateStatusAndComment() {
        OperationEntity entity = OperationEntity.builder()
                .id(3L)
                .status(OperationStatus.PENDING)
                .comment("Old Comment")
                .build();

        when(operationRepository.findById(3L)).thenReturn(Optional.of(entity));
        when(operationMapStruct.toResponse(any(OperationEntity.class))).thenReturn(new OperationResponse());

        OperationResponse result = operationService.approve(3L, "New Comment");

        assertThat(entity.getStatus()).isEqualTo(OperationStatus.APPROVED);
        assertThat(entity.getComment()).isEqualTo("New Comment");
        verify(operationRepository, times(1)).save(entity);
        assertThat(result).isNotNull();




    }



}
