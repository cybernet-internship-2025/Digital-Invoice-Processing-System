package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface InvoiceRepository {
    void saveInvoice(InvoiceEntity invoice);

    Optional<InvoiceEntity> findById(Long id);

    String findLastInvoiceNumberStartingWith(@Param("prefix") String prefix);

    void restoreInvoice(Long id);

    List<InvoiceEntity> findAll();

    void deleteInvoiceById(Long id);

    void updateInvoiceRecipientTaxId(String recipientTaxId);

    void changeStatus(@Param("invoiceId") Long invoiceId,
                      @Param("status") String status);
    Optional<InvoiceEntity> findBySenderTaxIdAndInvoiceId(
            @Param("senderTaxId") String senderTaxId,
            @Param("invoiceId") Long invoiceId
    );

    Optional<InvoiceEntity> findByIdAndReceiverTaxId(@Param("invoiceId") Long invoiceId,
                                                     @Param("receiverTaxId") String receiverTaxId);

    List<InvoiceEntity> findAllByRecipientUserTaxId(String recipientTaxId);

    void updateTotalPrice(@Param("invoiceId") Long invoiceId, @Param("totalPrice") BigDecimal totalPrice);

    List<InvoiceEntity> findInvoicesBySenderTaxId(String senderTaxId);
}
