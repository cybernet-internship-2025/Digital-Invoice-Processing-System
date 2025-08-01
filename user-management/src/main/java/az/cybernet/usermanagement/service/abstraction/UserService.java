package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll(Long limit);

    UserResponse addUser(CreateUserRequest request);

    UserResponse updateUser(String taxId, UpdateUserRequest request);

    UserResponse findUserByTaxId(String taxId);

    void restoreUser(String taxId);

    void deleteUser(String taxId);
}
