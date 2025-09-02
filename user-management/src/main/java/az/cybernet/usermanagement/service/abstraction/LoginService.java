package az.cybernet.usermanagement.service.abstraction;
import az.cybernet.usermanagement.dto.request.EnterCodeByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginByEmailRequest;
import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginRequest;

public interface LoginService {
    String loginByPhone(LoginRequest loginRequest);

    String loginByEmail(LoginByEmailRequest loginByEmailRequest);

    String loginByTelegram(LoginByTelegramRequest loginByTelegramRequest);

    boolean checkIsVerificationCodeSuccess(EnterCodeByTelegramRequest request);

    boolean validateCitizen(String pin,String phoneNumber);

}
