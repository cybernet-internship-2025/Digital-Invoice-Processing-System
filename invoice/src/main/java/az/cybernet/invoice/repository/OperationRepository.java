package az.cybernet.invoice.repository;

import az.cybernet.invoice.dto.request.create.CreateOperationRequest;
import az.cybernet.invoice.dto.response.OperationResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationRepository {

    OperationResponse findById(@Param("id") Long id);

    List<OperationResponse> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    void insert(@Param("operation") CreateOperationRequest operation);
}

