package az.cybernet.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperationEntity {
    private Long id;
    private Long invoiceId;
    private Long itemId;
    private String status;
    private String comment;
    private LocalDateTime createdAt;
}
