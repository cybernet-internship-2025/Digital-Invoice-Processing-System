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

    List<InvoiceEntity> getAll();

    void deleteInvoiceById(Long id);

    void updateInvoice(InvoiceEntity invoice);

    void changeStatus(Long id, String status);

    List<InvoiceEntity> findAllByStatus(String status);

    Optional<InvoiceEntity> findByIdAndBySenderTaxId(Long invoiceId, String senderTaxId);

    Optional<InvoiceEntity>findByIdAndReceiverTaxId(Long invoiceId, String receiverTaxId);

    List<InvoiceEntity> findAllByRecipientUserTaxId(String recipientTaxId);

    void updateTotalPrice(@Param("invoiceId") Long invoiceId, @Param("totalPrice") BigDecimal totalPrice);
}
