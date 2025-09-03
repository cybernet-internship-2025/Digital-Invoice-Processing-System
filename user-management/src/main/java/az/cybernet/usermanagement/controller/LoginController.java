package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.request.EnterCodeByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginByEmailRequest;
import az.cybernet.usermanagement.dto.request.LoginByTelegramRequest;
import az.cybernet.usermanagement.dto.request.LoginRequest;
import az.cybernet.usermanagement.service.abstraction.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login-by-phone")
    public void loginByPhone(@RequestBody @Valid LoginRequest loginRequest) {
         loginService.loginByPhone(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login-by-email")
    public void loginByEmail(@RequestBody @Valid LoginByEmailRequest loginByEmailRequest) {
          loginService.loginByEmail(loginByEmailRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/login-by-telegram-bot")
    public void loginByTelegram(@RequestBody @Valid LoginByTelegramRequest request) {
         loginService.loginByTelegram(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/verify-otp-code")
    public boolean verify(@RequestBody EnterCodeByTelegramRequest request) {
        return loginService.checkIsVerificationCodeSuccess(request);
    }
}

