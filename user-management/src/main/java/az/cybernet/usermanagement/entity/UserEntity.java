package az.cybernet.usermanagement.entity;


import az.cybernet.usermanagement.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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
    LocalDate dateOfBirth;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
