package az.cybernet.usermanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginByEmailRequest {
    @NotBlank
    @Size(min = 7, max = 7, message = "PIN must be exactly 7 characters")
    private String pin;

    @NotNull
    private String email;
}
