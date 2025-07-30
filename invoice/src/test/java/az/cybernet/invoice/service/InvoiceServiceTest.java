//package az.cybernet.invoice.service;
//
//import az.cybernet.invoice.client.UserClient;
//import az.cybernet.invoice.dto.client.user.UserResponse;
//import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
//import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
//import az.cybernet.invoice.entity.InvoiceEntity;
//import az.cybernet.invoice.mapper.InvoiceMapper;
//import az.cybernet.invoice.repository.InvoiceRepository;
//import az.cybernet.invoice.service.abstraction.ItemService;
//import az.cybernet.invoice.service.impl.InvoiceServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class InvoiceServiceTest {
//    @InjectMocks
//    InvoiceServiceImpl invoiceService;
//
//    @Mock
//    InvoiceRepository invoiceRepository;
//
//    @Mock
//    UserClient userClient;
//
//    @Mock
//    InvoiceMapper invoiceMapper;
//
//    @Mock
//    ItemService itemService;
//
//    @Test
//    void testSaveInvoice_successfullySavesInvoice() {
//        // given
//        var request = new CreateInvoiceRequest("1234567890", "0987654321");
//
//        var sender = new UserResponse(1L, "Sender", "1234567890", true, null, null);
//        var recipient = new UserResponse(2L, "Recipient", "0987654321", true, null, null);
//
//        var invoiceResponse = new InvoiceResponse();
//        invoiceResponse.setSenderTaxId("1234567890");
//        invoiceResponse.setRecipientTaxId("0987654321");
//
//        var invoiceEntity = new InvoiceEntity();
//
//        // when
//        when(userClient.findUserByTaxId("1234567890")).thenReturn(sender);
//        when(userClient.findUserByTaxId("0987654321")).thenReturn(recipient);
//        when(invoiceMapper.buildInvoiceResponse(request)).thenReturn(invoiceResponse);
//        when(invoiceMapper.buildInvoiceEntity(invoiceResponse)).thenReturn(invoiceEntity);
//
//        var result = invoiceService.saveInvoice(request);
//
//        // then
//        assertEquals("1234567890", result.getSenderTaxId());
//        assertEquals("0987654321", result.getRecipientTaxId());
//
//        verify(invoiceRepository).saveInvoice(invoiceEntity);
//    }
//}
