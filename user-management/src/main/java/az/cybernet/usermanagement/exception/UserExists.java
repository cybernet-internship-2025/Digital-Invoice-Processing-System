package az.cybernet.usermanagement.exception;

public class UserExists extends RuntimeException {
    String code;

    public UserExists(String code,String message) {
        super(message);
        this.code = code;



    }

}
