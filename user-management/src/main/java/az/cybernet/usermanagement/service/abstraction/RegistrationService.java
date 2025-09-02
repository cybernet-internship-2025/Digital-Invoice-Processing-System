package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.request.RegistrationRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;

public interface RegistrationService {
    UserResponse registerUser(Long id, RegistrationRequest request);

    UserResponse deactivateUser(Long id);

    UserResponse activateUser(Long id);
}
