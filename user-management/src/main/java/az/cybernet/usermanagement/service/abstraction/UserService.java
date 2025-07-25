package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

public interface UserService {
    public UserResponse findAll();
    public UserResponse addUser(CreateUserRequest request);
    public UserResponse updateUser(UpdateUserRequest request);


}
