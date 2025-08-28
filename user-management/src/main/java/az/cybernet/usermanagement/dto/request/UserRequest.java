package az.cybernet.usermanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 255)
    String name;

    @NotNull(message = "date of birth can not be empty")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth;
}
