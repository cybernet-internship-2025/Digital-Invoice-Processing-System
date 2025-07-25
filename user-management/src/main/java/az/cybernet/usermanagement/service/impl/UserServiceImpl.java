package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.mapper.UserMapper;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public List<UserResponse> findAll() {
        List<UserEntity> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }

    @Override
//    using transaction
    @Transactional
    public UserResponse addUser(CreateUserRequest request) {
        UserEntity userEntity = userMapper.toUserEntity(request);
        if(userRepository.findById(userEntity.getId()).isEmpty()) {
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(null);
            userEntity.setIsActive(true);
            String taxId= generateNextTaxId();
            userEntity.setTaxId(taxId);
            UserEntity db= userRepository.addUser(userEntity);
            return userMapper.toUserResponseFromEntity(userEntity);

        }

        throw  new RuntimeException("User already exists");

    }
//  using tarnsaction
    @Transactional
    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        UserEntity entity=userMapper.toUserEntityFromUpdate(request);
        entity.setUpdatedAt(LocalDateTime.now());
        String taxId= generateNextTaxId();
        entity.setTaxId(taxId);
        UserEntity db= userRepository.updateUser(entity);
        return userMapper.toUserResponseFromEntity(entity);
    }
    public String generateNextTaxId() {
        Long lastId = userRepository.findMaxTaxId();
        if (lastId == null) lastId = 0L;

        long nextId = lastId + 1;
        return String.format("%010d", nextId);
    }

}
