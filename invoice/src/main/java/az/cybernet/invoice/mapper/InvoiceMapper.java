package az.cybernet.invoice.mapper;

import az.cybernet.invoice.dto.request.invoice.CreateInvoiceRequest;
import az.cybernet.invoice.dto.response.invoice.InvoiceResponse;
import az.cybernet.invoice.dto.response.item.ItemResponse;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.entity.ItemEntity;
import az.cybernet.invoice.exception.InvalidTaxIdException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

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
    InvoiceResponse buildInvoiceResponse(CreateInvoiceRequest request);

    @Mapping(target = "senderTaxId", expression = "java(response.getSenderTaxId())")
    @Mapping(target = "recipientTaxId", expression = "java(response.getRecipientTaxId())")
    @Mapping(target = "items", expression = "java(mapItemResponsesToEntities(response.getItems()))")
    InvoiceEntity buildInvoiceEntity(InvoiceResponse response);

    List<InvoiceResponse> allByRecipientUserTaxId(List<InvoiceEntity> invoiceEntities);

    default Long parseTaxIdToLong(String taxId) {
        try {
            return Long.parseLong(taxId);
        } catch (NumberFormatException e) {
            throw new InvalidTaxIdException(taxId);
        }
    }

//    default List<ItemEntity> mapItemResponsesToEntities(List<ItemResponse> responses) {
//        return responses.stream()
//                .map(itemMapper::buildItemEntity)
//                .collect(Collectors.toList());
//    }

    InvoiceResponse fromEntityToResponse(InvoiceEntity invoice);

}
