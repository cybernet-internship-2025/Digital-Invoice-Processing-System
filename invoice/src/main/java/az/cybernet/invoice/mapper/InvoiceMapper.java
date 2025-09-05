package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceExportRequest;
import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.dto.response.invoice.FilterResponse;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderTaxId", source = "senderTaxId")
    @Mapping(target = "recipientTaxId", source = "recipientTaxId")
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java((java.time.LocalDateTime) null)")
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "invoiceNumber", ignore = true)
    @Mapping(target = "items", ignore = true)
    InvoiceEntity fromInvoiceRequestToEntity(CreateInvoiceRequest request);

    List<InvoiceResponse> allByRecipientUserTaxId(List<InvoiceEntity> invoiceEntities);

    InvoiceResponse fromEntityToResponse(InvoiceEntity invoice);

    @Mapping(target = "offset", ignore = true)
    @Mapping(target = "limit", ignore = true)
    InvoiceFilterRequest map(InvoiceExportRequest request);

    List<FilterResponse> allBySenderTaxId(List<InvoiceEntity> invoiceEntities);

}
