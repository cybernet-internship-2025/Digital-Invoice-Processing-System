package az.cybernet.usermanagement.entity;


import az.cybernet.usermanagement.enums.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserEntity {
    Long id;
    String name;
    String taxId;
    String userId;
    String password;
    Status status;
    String dateOfBirth;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
