package az.cybernet.invoice.service;

import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.request.item.UpdateItemRequest;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.entity.MeasurementEntity;
import az.cybernet.invoice.enums.ItemStatus;
import az.cybernet.invoice.mapper.ItemMapStruct;
import az.cybernet.invoice.repository.ItemRepository;
import az.cybernet.invoice.repository.MeasurementRepository;
import az.cybernet.invoice.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private ItemMapStruct itemMapStruct;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testUpdateItem_updatesSuccessfully() {
        // Prepare a request object
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setId(1L);
        updateRequest.setName("Updated Item");
        updateRequest.setUnitPrice(BigDecimal.valueOf(20));
        updateRequest.setQuantity(3);
        updateRequest.setMeasurementName("kg");

        // Mocked measurement entity returned from repo
        MeasurementEntity measurementEntity = new MeasurementEntity();
        measurementEntity.setName("kg");

        // Existing item fetched from repository
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setName("Old Name");

        // Define mocked behavior
     //   when(itemRepository.findById(1L)).thenReturn(Optional.of(itemEntity));
       // when(measurementRepository.findByName("kg")).thenReturn(measurementEntity);

        // Call the method under test
    //    itemService.updateItem(List.of(updateRequest));

        // Capture the updated item passed to repository
        ArgumentCaptor<ItemEntity> itemCaptor = ArgumentCaptor.forClass(ItemEntity.class);
    //    verify(itemRepository).updateItem(itemCaptor.capture());

        // Assert updated values
        ItemEntity updatedItem = itemCaptor.getValue();
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals(BigDecimal.valueOf(20), updatedItem.getUnitPrice());
        assertEquals(3, updatedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(60), updatedItem.getTotalPrice());
        assertEquals(ItemStatus.UPDATED, updatedItem.getStatus());
        assertEquals(measurementEntity, updatedItem.getMeasurement());
    }

    @Test
    void testFindAllItemsByInvoiceId_returnsMappedItems() {
        // Given an invoice ID
        Long invoiceId = 1L;

        // Two distinct ItemEntities
        ItemEntity item1 = new ItemEntity();
        item1.setId(1L);

        ItemEntity item2 = new ItemEntity();
        item2.setId(2L);

        // Mocked responses after mapping
        ItemResponse response1 = new ItemResponse();
        ItemResponse response2 = new ItemResponse();

        // Mock repository and mapping logic
        when(itemRepository.findAllItemsByInvoiceId(invoiceId)).thenReturn(List.of(item1, item2));
        when(itemMapStruct.toResponse(item1)).thenReturn(response1);
        when(itemMapStruct.toResponse(item2)).thenReturn(response2);

        // Call the method under test
        List<ItemResponse> result = itemService.findAllItemsByInvoiceId(invoiceId);

        // Assertions
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(response1, response2)));

        // Verify method calls and mapping
        verify(itemRepository).findAllItemsByInvoiceId(invoiceId);
        verify(itemMapStruct).toResponse(item1);
        verify(itemMapStruct).toResponse(item2);
        verify(itemMapStruct, times(2)).toResponse(any(ItemEntity.class));
    }

    @Test
    void addItemsTest() {
        ItemsRequest itemsRequest = new ItemsRequest();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setMeasurementName("kq"); // measurement name təyin edilir
        itemRequest.setUnitPrice(BigDecimal.valueOf(10));
        itemRequest.setQuantity(5);
        itemRequest.setProductName("Alma");
        itemsRequest.setItemsRequest(List.of(itemRequest));

        MeasurementEntity measurementEntity = new MeasurementEntity();
        measurementEntity.setId(1L);


    //    when(measurementRepository.findByName("kq")).thenReturn(measurementEntity);
      //  doNothing().when(itemRepository).addItems(any(ItemsRequest.class));

        List<ItemResponse> result = itemService.addItems(itemsRequest);

        assertNotNull(result);  // Make sure the result is not null
      //  verify(itemRepository).addItems(any(ItemsRequest.class)); // ensure addItems is called
    }

    @Test
    void deleteItemsTest() {

        Long invoiceId = 1L;
        doNothing().when(itemRepository).deleteItemsByInvoiceId(invoiceId);

        itemService.deleteItemsByInvoiceId(invoiceId);

        verify(itemRepository, times(1)).deleteItemsByInvoiceId(invoiceId); // ensure deleteItemsByInvoiceId is called
    }

    @Test
    void deleteItemsByItemsIdTest() {

        List<Long> ids = List.of(1L, 2L, 3L);
        doNothing().when(itemRepository).deleteItemsByItemsId(ids);

        itemService.deleteItemsByItemsId(ids);


        verify(itemRepository, times(1)).deleteItemsByItemsId(ids);
    }
}