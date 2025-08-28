package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

public interface UserService {

    UserResponse addUser(UserRequest request);

    UserResponse updateUser(String taxId, UserRequest request);

    UserResponse findUserByTaxId(String taxId);

    void restoreUser(String taxId);

    void deleteUser(String taxId);

    UserResponse activateUser(Long id);

    UserResponse deactivateUser(Long id);
}
