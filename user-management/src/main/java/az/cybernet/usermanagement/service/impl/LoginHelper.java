package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.entity.FinLoginData;
import az.cybernet.usermanagement.repository.OtpRepository;
import az.cybernet.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LoginHelper {

    private  final OtpRepository otpRepository;

    public void insertOTPLoginData(LoginByTelegramRequest request, String verificationCode) {
        FinLoginData loginData = FinLoginData.builder()
                .pin(request.getPin())
                .phoneNumber(request.getPhoneNumber())
                .verificationCode(verificationCode)
                .numberOfAttempts(0)
                .insertDate(
                        LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
                .state(false)
                .build();

        otpRepository.insertOTPLoginData(loginData);
    }

    public void updateLoginOTPData(String code, String pin, String phoneNumber) {
        otpRepository.updateLoginOTPData(code, pin, phoneNumber);
    }
}
