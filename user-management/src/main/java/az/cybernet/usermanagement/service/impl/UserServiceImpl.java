package az.cybernet.usermanagement.service.impl;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.repository.UserRepository;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    

    @Override
    public UserResponse findAll() {
        return null;
    }

    @Override
    public UserResponse addUser(CreateUserRequest request) {
        return null;
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        return null;
    }
}
