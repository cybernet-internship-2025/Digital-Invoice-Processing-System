package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemRepository {
    void addItem(ItemEntity item);

    void updateItem(ItemEntity item);

    Optional<ItemEntity> findById(Long id);

    List<ItemEntity> findAllItemsByInvoiceId(Long invoiceId);

    void deleteItem(@Param("list") List<Long> ids);

    void restoreItem(Long id);
}