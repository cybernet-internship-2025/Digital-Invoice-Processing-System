package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
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
import java.util.List;


import static az.cybernet.usermanagement.enums.ExceptionConstants.USER_NOT_FOUND;

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
        userRepository.restoreUser(user.getId());
        log.info("User with tax ID + " + taxId + " was restored!");
    }

    @Transactional
    @Override
    public void deleteUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.deleteUser(user.getId());
        log.info("User with tax ID + " + taxId + " was deleted!");
    }

    @Override
    public UserResponse findUserByTaxId(String taxId) {
        var user = fetchUserIfExist(taxId);
        return userMapstruct.toUserResponseFromEntity(user);
    }

    @Override
    public List<UserResponse> findAll(Long limit) {
        List<UserEntity> users = userRepository.findAll(limit);
        return userMapstruct.toUserResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse addUser(CreateUserRequest request) {
        UserEntity userEntity = userMapstruct.toUserEntityFromCreate(request);
        String taxId = generateNextTaxId();
        userEntity.setIsActive(true);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setTaxId(taxId);
        userRepository.addUser(userEntity);
        return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    @Transactional
    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        UserEntity userEntity = fetchUserIfExist(request.getTaxId());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setName(request.getName());
            userRepository.updateUser(userEntity);
            return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    private String generateNextTaxId() {
        Long lastId = userRepository.findMaxTaxId();
        if (lastId == null) lastId = 0L;

        long nextId = lastId + 1;
        return String.format("%010d", nextId);
    }


    public UserEntity fetchUserIfExist(String taxId) {
        return  userRepository.findUserByTaxId(taxId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));
    }

}
