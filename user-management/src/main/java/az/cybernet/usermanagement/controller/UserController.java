package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.request.LoginUserRequest;
import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.service.abstraction.UserLoginService;
import az.cybernet.usermanagement.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    UserLoginService loginService;

    @DeleteMapping("/{taxId}")
    @ResponseStatus(OK)
    public void deleteUser(@PathVariable("taxId") String taxId) {
        userService.deleteUser(taxId);
    }

    @PutMapping("/{taxId}/restore")
    @ResponseStatus(OK)
    public void restoreUser(@PathVariable("taxId") String taxId) {
        userService.restoreUser(taxId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserResponse addUser(@Valid @RequestBody UserRequest request) {
        return userService.addUser(request);
    }

    @PostMapping("/{id}/approve-registration")
    @ResponseStatus(OK)
    public UserResponse activateUser(@PathVariable Long id) {
        return userService.activateUser(id);
    }

    @PostMapping("/{id}/cancel-registration")
    @ResponseStatus(OK)
    public UserResponse deactivateUser(@PathVariable Long id) {
        return userService.deactivateUser(id);
    }

    @PutMapping("/{taxId}")
    @ResponseStatus(OK)
    public UserResponse updateUser(@Valid @RequestBody UserRequest request, @PathVariable("taxId") String taxId) {
        return userService.updateUser(taxId, request);
    }
    @PostMapping("/login")
    public boolean loginCitizen( @Valid @RequestBody LoginUserRequest request){

        return loginService.validateCitizen(request.getPin(), request.getPhoneNumber());

    }
}
