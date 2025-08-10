package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.aop.annotation.LogIgnore;
import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.InvalidTaxIdException;
import az.cybernet.usermanagement.exception.UserNotFoundException;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


import static az.cybernet.usermanagement.exception.ExceptionConstants.INVALID_TAX_ID_EXCEPTION;
import static az.cybernet.usermanagement.exception.ExceptionConstants.USER_NOT_FOUND;

@Log
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapstruct userMapstruct;

    @Transactional
    @Override
    public void restoreUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.restoreUser(user.getTaxId());
    }

    @Transactional
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


    @Transactional
    @Override
    public UserResponse addUser(UserRequest request) {
        UserEntity userEntity = userMapstruct.toUserEntityFromCreate(request);

        String taxId = generateNextTaxId();

        userEntity.setTaxId(taxId);
        userEntity.setIsActive(true);
        userEntity.setCreatedAt(LocalDateTime.now());
        userRepository.addUser(userEntity);
        return  userMapstruct.toUserResponseFromEntity(userEntity);
    }

    @Transactional
    @Override
    public UserResponse updateUser(String taxId, UserRequest request) {
        UserEntity userEntity = fetchUserIfExist(taxId);

        userEntity.setName(request.getName());
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

    @LogIgnore
    private UserEntity fetchUserIfExist(String taxId) {
        return  userRepository.findUserByTaxId(taxId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));
    }
}
