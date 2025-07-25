package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemRepository {
    void updateItem(ItemEntity item);

    List<ItemEntity> findAllItems();

    void restoreItemById(Long id);
}
