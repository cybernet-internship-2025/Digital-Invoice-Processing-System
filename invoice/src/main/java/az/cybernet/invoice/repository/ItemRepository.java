package az.cybernet.invoice.repository;

import az.cybernet.invoice.dto.request.item.ItemsRequest;
import az.cybernet.invoice.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemRepository {

    void addItems(@Param("itemsRequest") ItemsRequest itemsRequest);

    void updateItem(ItemEntity item);

    List<ItemEntity> findAllItemsByInvoiceId(Long invoiceId);

    //void deleteItem(@Param("list") List<Long> ids);

    Optional<ItemEntity> findById(Long id);

    void restoreItem(Long id);

    void deleteItemsByInvoiceId(Long invoiceId);

    void deleteItemsByItemsId(@Param("list") List<Long> ids);
}