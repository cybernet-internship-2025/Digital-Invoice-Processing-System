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

    @PostMapping("/login-by-telegram-bot")
    public ResponseEntity<Void> loginWithTelegram(@RequestBody @Valid LoginByTelegramRequest request) {
        loginService.loginByTelegram(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login-by-phone")
    public ResponseEntity<Void> loginWithPhone(@RequestBody @Valid LoginRequest loginRequest) {
        loginService.loginByPhone(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/login-by-email")
    public ResponseEntity<Void> loginWithEmail(@RequestBody @Valid LoginByEmailRequest loginByEmailRequest) {
        loginService.loginByEmail(loginByEmailRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/verify-otp-code")
    public boolean verify(@RequestBody EnterCodeByTelegramRequest request) {
        return loginService.checkIsVerificationCodeSuccess(request);
    }

}

