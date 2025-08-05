package az.cybernet.usermanagement.entity;


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
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
