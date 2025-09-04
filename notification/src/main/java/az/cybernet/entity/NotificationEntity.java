package az.cybernet.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NotificationEntity {
    Long id;
    Long invoiceId;
    String senderTaxId;
    String receiverTaxId;
    String operationType;
    String message;
    LocalDateTime createdAt;
    boolean isRead;
}
