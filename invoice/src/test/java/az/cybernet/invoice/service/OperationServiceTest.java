package az.cybernet.invoice.service;

import az.cybernet.invoice.dto.request.operation.CreateOperationDetailsRequest;
import az.cybernet.invoice.enums.OperationStatus;
import az.cybernet.invoice.mapper.OperationMapStruct;
import az.cybernet.invoice.repository.OperationRepository;
import az.cybernet.invoice.service.impl.OperationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {
    @InjectMocks
    private OperationServiceImpl operationService;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationMapStruct operationMapStruct;

  //  @Test
  //  OperationServiceImpl operationServiceimpl=new OperationServiceImpl();

    @Test
    void saveOperationtesting() {
        Long invoiceId = 3L;
        Long itemId = 10L;
        String comment = "Approved";

        CreateOperationDetailsRequest detailRequest = CreateOperationDetailsRequest.builder()
                .itemId(itemId)
                .itemStatus(OperationStatus.PENDING)
                .comment(comment)
                .build();




    }



}
