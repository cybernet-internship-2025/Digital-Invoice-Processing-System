package az.cybernet.usermanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(min = 7, max = 7, message = "PIN must be exactly 7 characters")
    private String pin;

    @Pattern(regexp = "\\+994?(10|50|51|55|60|70|77|99)[0-9]{7}", message = "INVALID_PHONE_NUMBER")
    private String phoneNumber;
}

