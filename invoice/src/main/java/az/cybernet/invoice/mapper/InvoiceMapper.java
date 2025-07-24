package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderTaxId", expression = "java(parseTaxIdToLong(request.getSenderTaxId()))")
    @Mapping(target = "recipientTaxId", expression = "java(parseTaxIdToLong(request.getRecipientTaxId()))")
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "invoiceNumber", ignore = true)
    @Mapping(target = "invoiceSeries", ignore = true)
    @Mapping(target = "items", ignore = true)
    InvoiceResponse toResponse(CreateInvoiceRequest request);

    default Long parseTaxIdToLong(String taxId) {
        try {
            return Long.parseLong(taxId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
