package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.OperationDetailsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationDetailsRepository {
    void save(OperationDetailsEntity entity);
    OperationDetailsEntity findByItemId(@Param("itemId") Long itemId);
    List<OperationDetailsEntity> findAll();
    void update(OperationDetailsEntity entity);
    void delete(@Param("itemId") Long itemId);

}
