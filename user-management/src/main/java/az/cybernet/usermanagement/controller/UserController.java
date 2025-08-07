package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.request.UserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;


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
    public UserResponse addUser(@Valid @RequestBody  UserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("/{taxId}")
    @ResponseStatus(OK)
    public UserResponse updateUser(@Valid @RequestBody UserRequest request, @PathVariable("taxId")  String taxId) {
        return userService.updateUser(taxId, request);
    }
}
