package az.cybernet.usermanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUserRequest {
    @NotBlank(message = " pin boş ola bilməz ")
    private String pin;
    @NotBlank(message = " nömrə boş ola bilməz ")
    private String phoneNumber;
}
