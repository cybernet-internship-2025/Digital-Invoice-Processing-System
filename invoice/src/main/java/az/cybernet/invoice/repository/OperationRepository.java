package az.cybernet.invoice.repository;

import az.cybernet.invoice.entity.OperationEntity;
import az.cybernet.invoice.enums.OperationStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OperationRepository {

    Optional<OperationEntity> findById(@Param("id") Long id);

    void save(@Param("op") OperationEntity operationEntity);

    List<OperationEntity> findAll();
    List<OperationEntity> findByStatus(@Param("status") OperationStatus status);
    List<OperationEntity> findAllItemsById(@Param("itemId") Long itemId);
    List<OperationEntity> findAllInvoicesById(@Param("invoiceId") Long invoiceId);


}