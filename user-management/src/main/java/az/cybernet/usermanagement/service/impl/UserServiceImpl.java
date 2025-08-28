package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.aop.annotation.LogIgnore;
import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.InvalidTaxIdException;
import az.cybernet.usermanagement.exception.NotFoundException;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static az.cybernet.usermanagement.enums.Status.APPROVED;
import static az.cybernet.usermanagement.enums.Status.PENDING;
import static az.cybernet.usermanagement.enums.Status.REJECTED;
import static az.cybernet.usermanagement.exception.ExceptionConstants.INVALID_TAX_ID_EXCEPTION;
import static az.cybernet.usermanagement.exception.ExceptionConstants.USER_NOT_FOUND;
import static lombok.AccessLevel.PRIVATE;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapstruct userMapstruct;
    PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void restoreUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.restoreUser(user.getTaxId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.deleteUser(user.getTaxId());
    }

    @Override
    public UserResponse findUserByTaxId(String taxId) {
        var user = fetchUserIfExist(taxId);
        return userMapstruct.toUserResponseFromEntity(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse addUser(UserRequest request) {
        var userEntity = userMapstruct.toUserEntityFromCreate(request);
        userEntity.setDateOfBirth(request.getDateOfBirth());
        userEntity.setName(request.getName());

        userRepository.addUser(userEntity);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse updateUser(String taxId, UserRequest request) {
        var userEntity = fetchUserIfExist(taxId);

        userMapstruct.updateUserFromRequest(request, userEntity);

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

    @LogIgnore
    private UserEntity fetchUserIfExist(String taxId) {
        return userRepository.findUserByTaxId(taxId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage(taxId)));
    }
}
