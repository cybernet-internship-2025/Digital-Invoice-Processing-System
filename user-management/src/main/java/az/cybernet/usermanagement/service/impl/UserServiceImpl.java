package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.entity.UserEntity;
import az.cybernet.usermanagement.mapper.UserMapper;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
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
    public UserResponse addUser(CreateUserRequest request) {
        UserEntity userEntity = userMapper.toUserEntityFromCreate(request);
        String taxId = generateNextTaxId();
        userEntity.setTaxId(taxId);
        userRepository.addUser(userEntity);
        return userMapper.toUserResponseFromEntity(userEntity);
    }

    //  using tarnsaction
    @Transactional
    @Override
    public UserResponse updateUser(UpdateUserRequest request) {

        UserEntity entity = userMapper.toUserEntityFromUpdate(request);
        entity.setUpdatedAt(LocalDateTime.now());
        String taxId = generateNextTaxId();
        entity.setTaxId(taxId);
        userRepository.updateUser(entity);
        return userMapper.toUserResponseFromEntity(entity);
    }

    private String generateNextTaxId() {
        Long lastId = userRepository.findMaxTaxId();
        if (lastId == null) lastId = 0L;

        long nextId = lastId + 1;
        return String.format("%010d", nextId);
    }

}
