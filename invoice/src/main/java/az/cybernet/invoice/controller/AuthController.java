package az.cybernet.invoice.controller;

import az.cybernet.invoice.dto.request.login.LoginRequestDto;
import az.cybernet.invoice.dto.response.login.LoginResponseDto;
import az.cybernet.invoice.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}