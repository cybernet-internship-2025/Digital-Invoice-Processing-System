package az.cybernet.invoice.dto.client.user;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String userId;
    private String password;
    private Boolean isBlocked;
    private Integer failedAttempts;
    private LocalDateTime blockedUntil;
    private String phoneNumber;
    private Boolean mustChangePassword;

}
