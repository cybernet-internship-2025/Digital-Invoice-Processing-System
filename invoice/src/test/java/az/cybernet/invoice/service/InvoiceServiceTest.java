package az.cybernet.invoice.service;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.service.abstraction.ItemService;
import az.cybernet.invoice.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @InjectMocks
    InvoiceServiceImpl invoiceService;

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    UserClient userClient;

    @Mock
    InvoiceMapper invoiceMapper;

    @Mock
    ItemService itemService;

    private CreateInvoiceRequest emptyItemsRequest(String recipientTin) {
        return CreateInvoiceRequest.builder()
                .recipientTaxId(recipientTin)
                .items(null)
                .build();
    }

    private CreateInvoiceRequest withItemsRequest(String recipientTin, List<ItemRequest> items) {
        var req = new CreateInvoiceRequest();
        req.setRecipientTaxId(recipientTin);

        var itemsReq = new ItemsRequest();
        itemsReq.setItemsRequest(items);

        req.setItems(itemsReq);
        return req;
    }

    @Test
    void saveInvoice_withoutItems_persistsAndReturnsResponse() {
        // given
        var recipientTin = "0987654321";
        var senderTin = "1234567890";

        var request = emptyItemsRequest(recipientTin);

        var recipient = new UserResponse(2L, "Recipient", recipientTin, true, null, null);

        var entity = new InvoiceEntity();
        entity.setSenderTaxId(senderTin);
        entity.setRecipientTaxId(recipientTin);
        entity.setTotalPrice(ZERO);

        var response = new InvoiceResponse();
        response.setSenderTaxId(senderTin);
        response.setRecipientTaxId(recipientTin);
        response.setTotalPrice(ZERO);

        // when
        when(userClient.findUserByTaxId(recipientTin)).thenReturn(recipient);

        when(invoiceMapper.fromInvoiceRequestToEntity(request)).thenReturn(entity);

        doAnswer(inv -> {
            entity.setId(42L);
            return null;
        }).when(invoiceRepository).saveInvoice(entity);

        when(invoiceMapper.fromEntityToResponse(entity)).thenReturn(response);

        var result = invoiceService.saveInvoice(request);

        // then
        assertEquals(senderTin, result.getSenderTaxId());
        assertEquals(recipientTin, result.getRecipientTaxId());
        assertEquals(ZERO, result.getTotalPrice());

        verify(invoiceRepository).saveInvoice(entity);
        verify(invoiceMapper).fromEntityToResponse(entity);
        verify(invoiceRepository, never()).updateTotalPrice(anyLong(), any());
        verify(itemService, never()).addItems(any());
    }

    @Test
    void saveInvoice_withItems_updatesTotalPrice() {
        // given
        var recipientTin = "0987654321";
        var senderTin = "1234567890";

        var item1 = new ItemRequest();
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("10.00"));

        var item2 = new ItemRequest();
        item2.setQuantity(3);
        item2.setUnitPrice(new BigDecimal("5.50"));

        var request = withItemsRequest(recipientTin, List.of(item1, item2));

        var recipient = new UserResponse(2L, "Recipient", recipientTin, true, null, null);

        var entity = new InvoiceEntity();
        entity.setSenderTaxId(senderTin);
        entity.setRecipientTaxId(recipientTin);
        entity.setTotalPrice(ZERO);

        var savedItem1 = new ItemResponse();
        savedItem1.setId(101L);
        savedItem1.setQuantity(2);
        savedItem1.setUnitPrice(new BigDecimal("10.00"));

        var savedItem2 = new ItemResponse();
        savedItem2.setId(102L);
        savedItem2.setQuantity(3);
        savedItem2.setUnitPrice(new BigDecimal("5.50"));

        var finalResponse = new InvoiceResponse();
        finalResponse.setSenderTaxId(senderTin);
        finalResponse.setRecipientTaxId(recipientTin);
        finalResponse.setTotalPrice(new BigDecimal("36.50"));

        // when
        when(userClient.findUserByTaxId(recipientTin)).thenReturn(recipient);
        when(invoiceMapper.fromInvoiceRequestToEntity(request)).thenReturn(entity);

        doAnswer(inv -> {
            entity.setId(99L);
            return null;
        }).when(invoiceRepository).saveInvoice(entity);

        // item saving
        when(itemService.addItems(any(ItemsRequest.class)))
                .thenReturn(List.of(savedItem1, savedItem2));

        // used by updateInvoiceTotalPrice(...)
        when(itemService.findAllItemsByInvoiceId(99L))
                .thenReturn(List.of(savedItem1, savedItem2));

        when(invoiceMapper.fromEntityToResponse(entity)).thenReturn(finalResponse);

        var result = invoiceService.saveInvoice(request);

        // then
        assertEquals(new BigDecimal("36.50"), result.getTotalPrice());

        // verify repo total update got the right number
        ArgumentCaptor<BigDecimal> totalCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(invoiceRepository).updateTotalPrice(eq(99L), totalCaptor.capture());
        assertEquals(new BigDecimal("36.50"), totalCaptor.getValue());

        verify(itemService).addItems(any(ItemsRequest.class));
        verify(invoiceMapper).fromEntityToResponse(entity);
    }

    @Test
    void saveInvoice_withInvalidItemQuantity_throws() {
        // given
        var recipientTin = "0987654321";
        var badItem = new ItemRequest();
        badItem.setQuantity(0); // invalid
        badItem.setUnitPrice(new BigDecimal("1.00"));

        var request = withItemsRequest(recipientTin, List.of(badItem));

        var recipient = new UserResponse(2L, "Recipient", recipientTin, true, null, null);
        var entity = new InvoiceEntity();

        // when
        when(userClient.findUserByTaxId(recipientTin)).thenReturn(recipient);
        when(invoiceMapper.fromInvoiceRequestToEntity(request)).thenReturn(entity);
        doAnswer(inv -> {
            entity.setId(1L);
            return null;
        }).when(invoiceRepository).saveInvoice(entity);

        // then
        assertThrows(IllegalArgumentException.class, () -> invoiceService.saveInvoice(request));
        verify(itemService, never()).addItems(any());
        verify(invoiceRepository, never()).updateTotalPrice(anyLong(), any());
    }
}
