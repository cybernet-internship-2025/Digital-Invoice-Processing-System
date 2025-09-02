package az.cybernet.invoice.service.abstraction;

import az.cybernet.invoice.dto.request.login.LoginRequestDto;
import az.cybernet.invoice.dto.response.login.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto req);
}