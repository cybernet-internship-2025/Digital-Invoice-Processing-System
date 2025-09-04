package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.aop.annotation.Log;
import az.cybernet.usermanagement.aop.annotation.LogIgnore;
import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.exception.NotFoundException;
import az.cybernet.usermanagement.mapper.UserMapstruct;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static az.cybernet.usermanagement.exception.ExceptionConstants.USER_NOT_FOUND;
import static lombok.AccessLevel.PRIVATE;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapstruct userMapstruct;

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
    public UserResponse updateUser(String taxId, UserRequest request) {
        var userEntity = fetchUserIfExist(taxId);

        userMapstruct.updateUserFromRequest(request, userEntity);

        userRepository.updateUser(userEntity);
        return userMapstruct.toUserResponseFromEntity(userEntity);
    }

    @LogIgnore
    private UserEntity fetchUserIfExist(String taxId) {
        return userRepository.findUserByTaxId(taxId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage(taxId)));
    }
}
