package az.cybernet.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinLoginData {

    private Long id;
    private String insertDate;
    private String pin;
    private String phoneNumber;
    private boolean state;
    private String verificationCode;
    private Integer numberOfAttempts;
}
