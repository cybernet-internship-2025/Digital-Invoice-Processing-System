package az.cybernet.usermanagement.exception;

import lombok.Getter;

@Getter
public class UserExists extends  RuntimeException {
    private final String code;

    public UserExists(String code, String message) {
        super(message);
        this.code = code;
    }

}
