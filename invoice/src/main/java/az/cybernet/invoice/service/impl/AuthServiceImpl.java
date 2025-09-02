package az.cybernet.invoice.service.impl;

import az.cybernet.invoice.dto.client.user.UserDto;
import az.cybernet.invoice.dto.client.user.UserResponse;
import az.cybernet.invoice.dto.request.login.LoginRequestDto;
import az.cybernet.invoice.dto.response.login.LoginResponseDto;
import az.cybernet.invoice.repository.UserRepository;
import az.cybernet.invoice.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto req) {

        if (!req.isValidUserIdOrTaxId()) {
            return new LoginResponseDto(false, "User ID (FIN) veya Tax ID is incorrect.", null, null, false);
        }

        if (!req.isValidPhone()) {
            return new LoginResponseDto(false, "Phone number is incorrect.", null, null, false);
        }

        var userOpt = userRepository.findByUserIdOrTaxId(req.getUserId(),
                req.getTaxId());
        if (userOpt.isEmpty()) {
            return new LoginResponseDto(false, "User is not found.", null, null, false);
        }

        UserDto user = userOpt.get();

        if (Boolean.TRUE.equals(user.getIsBlocked()) &&
                user.getBlockedUntil() != null &&
                user.getBlockedUntil().isAfter(LocalDateTime.now())) {
            return new LoginResponseDto(false, "Account is blocked.", null, user.getBlockedUntil(), false);
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            int attempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= 5) {
                user.setIsBlocked(true);
                user.setBlockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userRepository.update(user);
            return new LoginResponseDto(false, "Password is incorrect. Number of Attempts: " + attempts, null, user.getBlockedUntil(), false);
        }

        user.setFailedAttempts(0);
        user.setIsBlocked(false);
        user.setBlockedUntil(null);
        userRepository.update(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName("User Name");
        userResponse.setTaxId(user.getTaxId());      // taxId artık login response içinde de mevcut
        userResponse.setIsActive(true);
        userResponse.setCreatedAt(LocalDateTime.now().minusDays(1));
        userResponse.setUpdatedAt(LocalDateTime.now());


        if (Boolean.TRUE.equals(user.getMustChangePassword())) {
            return new LoginResponseDto(true,
                    "Login successful, but you must change your password.",
                    userResponse,
                    null,
                    true);
        }

        return new LoginResponseDto(true,
                "Login is successful",
                userResponse,
                null,
                false);
    }
}
