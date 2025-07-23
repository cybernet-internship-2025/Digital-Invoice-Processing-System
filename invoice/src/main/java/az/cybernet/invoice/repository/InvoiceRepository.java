package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface InvoiceRepository {
    void saveInvoice(InvoiceEntity invoice);

    Optional<InvoiceEntity> findById(Long id);

    void restoreInvoice(Long id);

}
