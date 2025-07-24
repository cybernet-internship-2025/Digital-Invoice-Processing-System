package az.cybernet.invoice.repository;


import az.cybernet.invoice.entity.ItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemRepository {
    void addItem(ItemEntity item);

    ItemEntity findById(Long id);

    void deleteItem(@Param("list") List<Long> ids);

}