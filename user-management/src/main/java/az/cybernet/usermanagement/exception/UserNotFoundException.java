package az.cybernet.usermanagement.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String code;

    public UserNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
}
