package az.cybernet.invoice.dto.response.login;

import az.cybernet.invoice.dto.client.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private boolean success;
    private String message;
    private UserResponse user;
    private LocalDateTime blockedUntil;
    private Boolean mustChangePassword;

}