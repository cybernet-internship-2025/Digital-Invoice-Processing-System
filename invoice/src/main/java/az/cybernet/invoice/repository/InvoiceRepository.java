package az.cybernet.invoice.repository;

import az.cybernet.invoice.dto.request.invoice.InvoiceFilterRequest;
import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.enums.InvoiceStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface InvoiceRepository {
    void saveInvoice(InvoiceEntity invoice);

    Optional<InvoiceEntity> findById(@Param("id") Long id);

    Long getNextInvoiceSequence();

    void updateInvoiceStatus(@Param("id") Long id, @Param("status") InvoiceStatus status, @Param("updatedAt") LocalDateTime updatedAt);


    void deleteInvoiceById(Long id);


    void deleteInvoicesById(List<Long> invoiceIds);


    void updateInvoiceRecipientTaxId(@Param("invoiceId") Long InvoiceId,
                                     @Param("recipientTaxId") String recipientTaxId);

    void changeStatus(@Param("invoiceId") Long invoiceId,
                      @Param("status") String status);

    Optional<InvoiceEntity> findBySenderTaxIdAndInvoiceId(
            @Param("senderTaxId") String senderTaxId,
            @Param("invoiceId") Long invoiceId
    );

    Optional<InvoiceEntity> findByIdAndReceiverTaxId(@Param("invoiceId") Long invoiceId,
                                                     @Param("receiverTaxId") String receiverTaxId);


    List<InvoiceEntity> findAllInvoicesByRecipientUserTaxId(@Param("recipientTaxId") String recipientTaxId,
                                                            @Param("filter") InvoiceFilterRequest filter);

    Long countInvoicesByRecipientUserTaxId(@Param("recipientTaxId") String recipientTaxId,
                                           @Param("filter") InvoiceFilterRequest filter);

    void updateTotalPrice(@Param("invoiceId") Long invoiceId, @Param("totalPrice") BigDecimal totalPrice);

    List<InvoiceEntity> findInvoicesBySenderTaxId(@Param("senderTaxId") String senderTaxId,
                                                  @Param("filter") InvoiceFilterRequest filter);


    void refreshInvoice(Long invoiceId);




    List<Long> findInvalidInvoiceIdsBySenderTaxId(@Param("senderTaxId") String senderTaxId,
                                   @Param("invoicesId") List<Long> invoiceIds);

    List<Long>findAllInvalidInvoicesById(@Param("invoiceIds") List<Long> invoiceIds);

    List<InvoiceEntity>findInvoicesByIds(@Param("ids") List<Long>ids);

    void updateStatuses(@Param("ids") List<Long> invoiceIds,
                        @Param("status") String status);
}

