package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.dto.request.RegistrationRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.InvalidTaxIdException;
import az.cybernet.usermanagement.exception.NotFoundException;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static az.cybernet.usermanagement.enums.Status.*;
import static az.cybernet.usermanagement.exception.ExceptionConstants.INVALID_TAX_ID_EXCEPTION;
import static az.cybernet.usermanagement.exception.ExceptionConstants.USER_NOT_FOUND;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationServiceImpl implements RegistrationService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapstruct userMapstruct;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse registerUser(Long id, RegistrationRequest request) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage())
        );

        userEntity.setRegistrationType(request.getType());
        userEntity.setStatus(PENDING);
        userEntity.setIsActive(false);
        userEntity.setUpdatedAt(null);

        return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse deactivateUser(Long id) {
        var userEntity = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (userEntity.getStatus() != PENDING) {
            throw new IllegalStateException("Only users in PENDING status can be canceled");
        }

        userEntity.setStatus(REJECTED);
        userEntity.setIsActive(false);
        userEntity.setUpdatedAt(LocalDateTime.now());

        userRepository.updateUser(userEntity);

        return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse activateUser(Long id) {
        var userEntity = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (userEntity.getStatus() != PENDING) {
            throw new IllegalStateException("User is not in PENDING status");
        }

        String taxId = generateNextTaxId();
        String userId = userRepository.generateUserId();
        LocalDate dob = userEntity.getDateOfBirth();

        if (dob == null) {
            throw new IllegalStateException("User dateOfBirth is null");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String password = dob.format(formatter);
        String passwordHash = passwordEncoder.encode(password);

        userEntity.setTaxId(taxId);
        userEntity.setUserId(userId);
        userEntity.setPassword(passwordHash);
        userEntity.setStatus(APPROVED);
        userEntity.setIsActive(true);
        userEntity.setUpdatedAt(LocalDateTime.now());

        userRepository.updateUser(userEntity);

        return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    private String generateNextTaxId() {
        String lastTaxId = userRepository.findMaxTaxId();

        if (lastTaxId == null || lastTaxId.isBlank()) {
            lastTaxId = "0";
        }

        long lastId;

        try {
            lastId = Long.parseLong(lastTaxId);
        } catch (NumberFormatException e) {
            throw new InvalidTaxIdException(INVALID_TAX_ID_EXCEPTION.getCode(), INVALID_TAX_ID_EXCEPTION.getMessage());
        }

        long nextId = lastId + 1;
        return String.format("%010d", nextId);
    }
}
