package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.InvoiceEntity;
import az.cybernet.invoice.enums.InvoiceStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    void restoreInvoice(Long id);

    List<InvoiceEntity> getAll();

    void deleteInvoiceById(Long id);

    void updateInvoice(InvoiceEntity invoice);

    void changeStatus(Long id, String status);

    List<InvoiceEntity> findAllByStatus(String status);

    Optional<InvoiceEntity> findByIdAndBySenderTaxId(Long invoiceId, String senderTaxId);

    Optional<InvoiceEntity> findByIdAndReceiverTaxId(Long invoiceId, String receiverTaxId);

    List<InvoiceEntity> findAllInvoicesByRecipientUserTaxId(String recipientTaxId);

    void updateTotalPrice(@Param("invoiceId") Long invoiceId, @Param("totalPrice") BigDecimal totalPrice);
}
