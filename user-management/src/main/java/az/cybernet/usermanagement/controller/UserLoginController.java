package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.request.EnterCodeByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginByEmailRequest;
import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginRequest;
import az.cybernet.usermanagement.service.abstraction.UserLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserLoginService loginService;

    @PostMapping("/login-by-phone")
    public String loginByPhone(@RequestBody @Valid LoginRequest loginRequest) {
        return loginService.loginByPhone(loginRequest);
    }

    @PostMapping("/login-by-email")
    public String loginByEmail(@RequestBody @Valid LoginByEmailRequest loginByEmailRequest) {
        return loginService.loginByEmail(loginByEmailRequest);
    }

    @PostMapping("/login-by-telegram-bot")
    public String loginByTelegram(@RequestBody @Valid LoginByTelegramRequest request) {
        return loginService.loginByTelegram(request);
    }

    @PostMapping("/verify-otp-code")
    public String verify(@RequestBody EnterCodeByTelegramRequest request) {
        return loginService.checkIsVerificationCodeSuccess(request);
    }

}

