package az.cybernet.usermanagement.exception;

import lombok.Getter;

@Getter
public class InvalidTaxIdException extends RuntimeException{
    private final String code;

    public InvalidTaxIdException(String code, String message) {
        super(message);
        this.code = code;
    }
}
