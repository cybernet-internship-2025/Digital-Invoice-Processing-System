package az.cybernet.usermanagement.exception;

public class UserExists extends RuntimeException {
    String code;

    public UserExists(String code) {
        this.code = code;

    }

}
