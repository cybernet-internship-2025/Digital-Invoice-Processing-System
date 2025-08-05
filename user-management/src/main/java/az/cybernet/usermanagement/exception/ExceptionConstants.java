package az.cybernet.usermanagement.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionConstants {
    USER_NOT_FOUND("USER_NOT_FOUND", "User with provided id was not found!"),
    VALIDATION_EXCEPTION("VALIDATION_EXCEPTION", "Validation exception happened!"),
    INVALID_TAX_ID_EXCEPTION("INVALID_TAX_ID_EXCEPTION", "Invalid Tax ID in the database!");

    private final String code;
    private final String message;
}

