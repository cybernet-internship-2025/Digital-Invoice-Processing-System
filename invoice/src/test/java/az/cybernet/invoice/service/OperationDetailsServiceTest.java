package az.cybernet.invoice.service;

import az.cybernet.invoice.dto.response.operation.OperationDetailsResponse;
import az.cybernet.invoice.entity.OperationDetailsEntity;
import az.cybernet.invoice.mapper.OperationDetailsMapStruct;
import az.cybernet.invoice.repository.OperationDetailsRepository;
import az.cybernet.invoice.service.impl.OperationDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OperationDetailsServiceTest {


    @Mock
    OperationDetailsRepository operationDetailsRepository;

    @Mock
    OperationDetailsMapStruct operationDetailsMapStruct;

    @InjectMocks
    OperationDetailsServiceImpl operationDetailsService;


    @Test
    void testSave(){
        OperationDetailsEntity entity = new OperationDetailsEntity();
        OperationDetailsResponse response = new OperationDetailsResponse();

        doNothing().when(operationDetailsRepository).save(entity);
        when(operationDetailsMapStruct.toResponse(entity)).thenReturn(response);

        OperationDetailsResponse result = operationDetailsService.save(entity);

        verify(operationDetailsRepository, times(1)).save(entity);
        verify(operationDetailsMapStruct, times(1)).toResponse(entity);

        assertEquals(response, result);
}

    @Test
    void testFindByItemId(){
        Long itemId = 1L;
        OperationDetailsEntity entity = new OperationDetailsEntity();
        OperationDetailsResponse response = new OperationDetailsResponse();

        when(operationDetailsRepository.findByItemId(itemId)).thenReturn(entity);
        when(operationDetailsMapStruct.toResponse(entity)).thenReturn(response);

        OperationDetailsResponse result = operationDetailsService.findByItemId(itemId);

        verify(operationDetailsRepository).findByItemId(itemId);
        verify(operationDetailsMapStruct).toResponse(entity);
        assertEquals(response,result);
    }

    @Test
    void testFindAll(){
        List<OperationDetailsEntity> entityList = new ArrayList<>();
        List<OperationDetailsResponse> responseList = new ArrayList<>();

        when(operationDetailsMapStruct.toResponseList(entityList)).thenReturn(responseList);
        when(operationDetailsRepository.findAll()).thenReturn(entityList);

        List<OperationDetailsResponse> result = operationDetailsService.findAll();

        assertEquals(responseList, result);

    }




}
