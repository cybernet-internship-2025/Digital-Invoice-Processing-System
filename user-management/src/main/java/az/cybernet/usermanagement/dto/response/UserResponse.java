package az.cybernet.usermanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
public class UserResponse {
    Long id;
    String name;
    String taxId;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;


}
