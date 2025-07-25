package az.cybernet.usermanagement.service.abstraction;

import az.cybernet.usermanagement.dto.response.UserResponse;

public interface UserService {

    UserResponse findUserByTaxId(String taxId);

    void restoreUser(String taxId);

    void deleteUser(String taxId);
}
