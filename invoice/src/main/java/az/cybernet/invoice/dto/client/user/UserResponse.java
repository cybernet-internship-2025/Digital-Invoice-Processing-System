package az.cybernet.invoice.dto.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserResponse {
    Long id;
    String name;
    String taxId;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}