package az.cybernet.invoice.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= PRIVATE)
public class UserEntity {
    Long id;
    String name;
    String taxId;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
