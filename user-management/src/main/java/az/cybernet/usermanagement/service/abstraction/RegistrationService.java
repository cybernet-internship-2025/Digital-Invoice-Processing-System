package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

public interface RegistrationService {
    UserResponse addUser(UserRequest request);

    UserResponse deactivateUser(Long id);

    UserResponse activateUser(Long id);
}
