package az.cybernet.usermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Otp {
    private String verificationCode;
    private int attemptCount;
}