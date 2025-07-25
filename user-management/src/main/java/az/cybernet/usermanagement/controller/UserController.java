package az.cybernet.usermanagement.controller;

import az.cybernet.usermanagement.dto.response.UserResponse;
import az.cybernet.usermanagement.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
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
    public void deleteUser(@PathVariable String taxId) {
        userService.deleteUser(taxId);
    }

    @PutMapping("/{taxId}/restore")
    @ResponseStatus(OK)
    public void restoreUser(@PathVariable String taxId) {
        userService.restoreUser(taxId);
    }
}
