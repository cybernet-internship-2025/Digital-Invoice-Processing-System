package az.cybernet.invoice.service;

import az.cybernet.invoice.client.UserClient;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.invoice.ApproveAndCancelInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.RequestCorrectionRequest;
import az.cybernet.invoice.dto.request.item.ItemRequest;
import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.enums.InvoiceStatus;
import az.cybernet.invoice.exception.InvalidStatusException;
import az.cybernet.invoice.exception.NotFoundException;
import az.cybernet.invoice.exception.UnauthorizedException;
import az.cybernet.invoice.mapper.InvoiceMapper;
import az.cybernet.invoice.repository.InvoiceRepository;
import az.cybernet.invoice.service.abstraction.ItemService;
import az.cybernet.invoice.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static az.cybernet.invoice.exception.ExceptionConstants.INVALID_STATUS;
import static az.cybernet.invoice.exception.ExceptionConstants.INVOICE_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.RECIPIENT_NOT_FOUND;
import static az.cybernet.invoice.exception.ExceptionConstants.UNAUTHORIZED;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private static final String VALID_TAX_ID = "0000000002";
    private static final String INVALID_TAX_ID = "9999999999";

    private static final Long INVOICE_ID = 1L;
    private static final String SENDER_TAX_ID = "0000000001";
    private static final String RECIPIENT_TAX_ID = "0000000002";

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

    // saveInvoice method
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

    // findById method
    @Test
    void testFindById_WhenInvoiceExists_ShouldReturnInvoiceResponse() {
        // given
        Long invoiceId = 1L;
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setId(invoiceId);

        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setId(invoiceId);

        // when
        Mockito.when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoiceEntity));
        Mockito.when(invoiceMapper.fromEntityToResponse(invoiceEntity)).thenReturn(invoiceResponse);

        // then
        InvoiceResponse result = invoiceService.findById(invoiceId);

        assertNotNull(result);
        assertEquals(invoiceId, result.getId());
        Mockito.verify(invoiceRepository).findById(invoiceId);
        Mockito.verify(invoiceMapper).fromEntityToResponse(invoiceEntity);
    }

    @Test
    void testFindById_WhenInvoiceNotFound_ShouldThrowNotFoundException() {
        // given
        Long invoiceId = 99L;

        //when
        Mockito.when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            invoiceService.findById(invoiceId);
        });

        assertEquals(INVOICE_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(INVOICE_NOT_FOUND.getMessage(), exception.getMessage());
        Mockito.verify(invoiceRepository).findById(invoiceId);
        Mockito.verifyNoInteractions(invoiceMapper);
    }


    // findAllByRecipientUserTaxId
    @Test
    void testFindAllByRecipientUserTaxId_WhenUserExists_ShouldReturnInvoiceList() {
        // Given
        UserResponse recipient = new UserResponse();
        recipient.setTaxId(VALID_TAX_ID);

        List<InvoiceEntity> entityList = List.of(new InvoiceEntity(), new InvoiceEntity());
        List<InvoiceResponse> responseList = List.of(new InvoiceResponse(), new InvoiceResponse());

        // When
        Mockito.when(userClient.findUserByTaxId(VALID_TAX_ID)).thenReturn(recipient);
        Mockito.when(invoiceRepository.findAllInvoicesByRecipientUserTaxId(VALID_TAX_ID)).thenReturn(entityList);
        Mockito.when(invoiceMapper.allByRecipientUserTaxId(entityList)).thenReturn(responseList);

        // Then
        List<InvoiceResponse> result = invoiceService.findAllByRecipientUserTaxId(VALID_TAX_ID);

        assertNotNull(result);
        assertEquals(2, result.size());

        Mockito.verify(userClient).findUserByTaxId(VALID_TAX_ID);
        Mockito.verify(invoiceRepository).findAllInvoicesByRecipientUserTaxId(VALID_TAX_ID);
        Mockito.verify(invoiceMapper).allByRecipientUserTaxId(entityList);
    }

    @Test
    void testFindAllByRecipientUserTaxId_WhenUserNotFound_ShouldThrowNotFoundException() {
        // When
        Mockito.when(userClient.findUserByTaxId(INVALID_TAX_ID)).thenReturn(null);

        // Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            invoiceService.findAllByRecipientUserTaxId(INVALID_TAX_ID);
        });

        assertEquals(RECIPIENT_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(RECIPIENT_NOT_FOUND.getMessage(), exception.getMessage());

        Mockito.verify(userClient).findUserByTaxId(INVALID_TAX_ID);
        Mockito.verifyNoMoreInteractions(invoiceRepository, invoiceMapper);
    }

    // approveInvoice
    @Test
    void testApproveInvoice_WhenValid_ShouldApproveSuccessfully() {
        // Given
        var invoiceEntity = new InvoiceEntity();
        invoiceEntity.setId(INVOICE_ID);
        invoiceEntity.setSenderTaxId(SENDER_TAX_ID);
        invoiceEntity.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoiceEntity.setStatus(InvoiceStatus.PENDING);

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        var item1 = new ItemResponse();
        item1.setId(10L);
        var item2 = new ItemResponse();
        item2.setId(20L);
        List<ItemResponse> items = List.of(item1, item2);

        // Mock dependencies
        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoiceEntity));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(items);

        // When
        invoiceService.approveInvoice(INVOICE_ID, request);

        // Then
        Mockito.verify(invoiceRepository).updateInvoiceStatus(Mockito.eq(INVOICE_ID), Mockito.eq(InvoiceStatus.APPROVED), Mockito.any());
        Mockito.verify(itemService).findAllItemsByInvoiceId(INVOICE_ID);
    }

    @Test
    void testApproveInvoice_WhenTaxIdsMismatch_ShouldThrowUnauthorizedException() {
        // Given
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.PENDING);

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId("WRONG_SENDER");
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        // When / Then
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                invoiceService.approveInvoice(INVOICE_ID, request)
        );

        assertEquals(UNAUTHORIZED.getCode(), ex.getCode());
        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testApproveInvoice_WhenStatusIsNotPending_ShouldThrowInvalidStatusException() {
        // Given
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.APPROVED); // already approved

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        // When / Then
        InvalidStatusException ex = assertThrows(InvalidStatusException.class, () ->
                invoiceService.approveInvoice(INVOICE_ID, request)
        );

        assertEquals(INVALID_STATUS.getCode(), ex.getCode());
        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testApproveInvoice_WhenItemsAreNull_ShouldStillApprove() {
        // Given
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.PENDING);

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        // Mock return
        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(null); // null list

        // When
        invoiceService.approveInvoice(INVOICE_ID, request);

        // Then
        Mockito.verify(invoiceRepository).updateInvoiceStatus(Mockito.eq(INVOICE_ID), Mockito.eq(InvoiceStatus.APPROVED), Mockito.any());
    }

    @Test
    void testApproveInvoice_WhenItemsAreEmpty_ShouldStillApprove() {
        // Given
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.PENDING);

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(Collections.emptyList());

        // When
        invoiceService.approveInvoice(INVOICE_ID, request);

        // Then
        Mockito.verify(invoiceRepository).updateInvoiceStatus(Mockito.eq(INVOICE_ID), Mockito.eq(InvoiceStatus.APPROVED), Mockito.any());
        Mockito.verify(itemService).findAllItemsByInvoiceId(INVOICE_ID);
    }


    // cancelInvoice
    @Test
    void testCancelInvoice_WhenValidWithItems_ShouldCancelAndDeleteItems() {
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.PENDING);

        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        var item1 = new ItemResponse();
        item1.setId(101L);
        var item2 = new ItemResponse();
        item2.setId(102L);
        var items = List.of(item1, item2);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(items);

        invoiceService.cancelInvoice(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CANCELED), any());
        Mockito.verify(itemService).deleteItem(List.of(101L, 102L));
    }

    @Test
    void testCancelInvoice_WhenValidWithNoItems_ShouldCancelAndSkipDeletion() {
        var invoice = createPendingInvoice();
        var request = createValidRequest();

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(Collections.emptyList());

        invoiceService.cancelInvoice(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CANCELED), any());
        Mockito.verify(itemService, Mockito.never()).deleteItem(any());
    }

    @Test
    void testCancelInvoice_WhenTaxIdMismatch_ShouldThrowUnauthorizedException() {
        var invoice = createPendingInvoice();
        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId("WRONG");
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        assertThrows(UnauthorizedException.class, () -> {
            invoiceService.cancelInvoice(INVOICE_ID, request);
        });

        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(any(), any(), any());
    }

    @Test
    void testCancelInvoice_WhenStatusNotPending_ShouldThrowInvalidStatusException() {
        var invoice = createPendingInvoice();
        invoice.setStatus(InvoiceStatus.APPROVED); // Not PENDING

        var request = createValidRequest();

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        assertThrows(InvalidStatusException.class, () -> {
            invoiceService.cancelInvoice(INVOICE_ID, request);
        });

        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(any(), any(), any());
    }

    @Test
    void testCancelInvoice_WhenItemsAreNull_ShouldLogCancelOnly() {
        var invoice = createPendingInvoice();
        var request = createValidRequest();

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(null);

        invoiceService.cancelInvoice(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CANCELED), any());
        Mockito.verify(itemService, Mockito.never()).deleteItem(any());
    }

    private InvoiceEntity createPendingInvoice() {
        var invoice = new InvoiceEntity();
        invoice.setId(INVOICE_ID);
        invoice.setSenderTaxId(SENDER_TAX_ID);
        invoice.setRecipientTaxId(RECIPIENT_TAX_ID);
        invoice.setStatus(InvoiceStatus.PENDING);
        return invoice;
    }

    private ApproveAndCancelInvoiceRequest createValidRequest() {
        var request = new ApproveAndCancelInvoiceRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);
        return request;
    }

    // requestCorrection
    @Test
    void testRequestCorrection_WithItemsAndComment_ShouldUpdateAndLogOperations() {
        var invoice = createPendingInvoice();
        var request = new RequestCorrectionRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);
        request.setComment("Please correct price");

        var item1 = new ItemResponse(); item1.setId(1L);
        var item2 = new ItemResponse(); item2.setId(2L);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(List.of(item1, item2));

        invoiceService.requestCorrection(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CORRECTION), any());
        Mockito.verify(itemService).findAllItemsByInvoiceId(INVOICE_ID);
    }

    @Test
    void testRequestCorrection_WithNullComment_ShouldUseDefaultComment() {
        var invoice = createPendingInvoice();
        var request = new RequestCorrectionRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);
        request.setComment(null);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(List.of());

        invoiceService.requestCorrection(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CORRECTION), any());
    }

    @Test
    void testRequestCorrection_WhenItemsAreNull_ShouldAddOperationOnce() {
        var invoice = createPendingInvoice();
        var request = createValidCorrectionRequest();

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        Mockito.when(itemService.findAllItemsByInvoiceId(INVOICE_ID)).thenReturn(null);

        invoiceService.requestCorrection(INVOICE_ID, request);

        Mockito.verify(invoiceRepository).updateInvoiceStatus(eq(INVOICE_ID), eq(InvoiceStatus.CORRECTION), any());
        Mockito.verify(itemService).findAllItemsByInvoiceId(INVOICE_ID);
    }

    @Test
    void testRequestCorrection_WhenTaxIdMismatch_ShouldThrowUnauthorizedException() {
        var invoice = createPendingInvoice();
        var request = new RequestCorrectionRequest();
        request.setSenderTaxId("WRONG");
        request.setRecipientTaxId(RECIPIENT_TAX_ID);

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        assertThrows(UnauthorizedException.class, () -> {
            invoiceService.requestCorrection(INVOICE_ID, request);
        });

        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(any(), any(), any());
    }

    @Test
    void testRequestCorrection_WhenStatusNotPending_ShouldThrowInvalidStatusException() {
        var invoice = createPendingInvoice();
        invoice.setStatus(InvoiceStatus.APPROVED); // Invalid state

        var request = createValidCorrectionRequest();

        Mockito.when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));

        assertThrows(InvalidStatusException.class, () -> {
            invoiceService.requestCorrection(INVOICE_ID, request);
        });

        Mockito.verify(invoiceRepository, Mockito.never()).updateInvoiceStatus(any(), any(), any());
    }


    private RequestCorrectionRequest createValidCorrectionRequest() {
        var request = new RequestCorrectionRequest();
        request.setSenderTaxId(SENDER_TAX_ID);
        request.setRecipientTaxId(RECIPIENT_TAX_ID);
        request.setComment("Fix item name");
        return request;
    }

}
