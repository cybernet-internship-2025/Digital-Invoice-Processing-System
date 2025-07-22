package az.cybernet.invoice.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class OperationEntity {
    private Long id;
    private Long invoiceId;
    private Long itemId;
    private String status;
    private String comment;
    private LocalDateTime createdAt;
}
