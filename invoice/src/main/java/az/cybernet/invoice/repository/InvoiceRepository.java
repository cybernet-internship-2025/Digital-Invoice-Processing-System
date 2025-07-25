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

    void updateTotalPrice(@Param("invoiceId") Long invoiceId, @Param("totalPrice") BigDecimal totalPrice);

    void restoreInvoice(Long id);

    List<InvoiceEntity> getAll();

}
