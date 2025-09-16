package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.EnterCodeByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginByEmailRequest;
import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;


public interface UserLoginService {

    void loginByPhone(LoginRequest loginRequest);

    void loginByEmail(LoginByEmailRequest loginByEmailRequest);

    void loginByTelegram(LoginByTelegramRequest loginByTelegramRequest);

    ResponseEntity<Map<String, String>> checkIsVerificationCodeSuccess(EnterCodeByTelegramRequest request);

    boolean validateCitizen(String pin, String phoneNumber);

}
