package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.UserNotFoundException;
import az.cybernet.usermanagement.mapper.UserMapper;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static az.cybernet.usermanagement.enums.ExceptionConstants.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public void restoreUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.restoreUser(user.getId());
        log.info("User with tax ID + " + taxId + " was restored!");
    }

    @Override
    public void deleteUser(String taxId) {
        var user = fetchUserIfExist(taxId);
        userRepository.deleteUser(user.getId());
        log.info("User with tax ID + " + taxId + " was deleted!");
    }

    @Override
    public UserResponse findUserByTaxId(String taxId) {
        var user = fetchUserIfExist(taxId);
        return userMapper.toUserResponseFromEntity(user);
    }

    private UserEntity fetchUserIfExist(String taxId) {
        return  userRepository.findUserByTaxId(taxId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));
    }

}
