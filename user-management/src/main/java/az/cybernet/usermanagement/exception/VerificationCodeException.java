package az.cybernet.usermanagement.exception;

import lombok.Getter;

@Getter
public class VerificationCodeException extends RuntimeException {
    private final int httpStatus;

    public VerificationCodeException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}