package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.request.CreateUserRequest;
import az.cybernet.usermanagement.dto.request.UpdateUserRequest;
import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping("/{taxId}")
    @ResponseStatus(OK)
    public UserResponse findUserByTaxId(@PathVariable("taxId") String taxId) {
        return userService.findUserByTaxId(taxId);
    }

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

    @GetMapping
    @ResponseStatus(OK)
    public List<UserResponse> findAllUsers(@RequestParam("limit") Long limit) {
        return userService.findAll(limit);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserResponse addUser(@Valid @RequestBody  CreateUserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("/{taxId}")
    @ResponseStatus(OK)
    public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest request, @PathVariable("taxId")  String taxId) {
        return userService.updateUser(taxId, request);
    }
}
