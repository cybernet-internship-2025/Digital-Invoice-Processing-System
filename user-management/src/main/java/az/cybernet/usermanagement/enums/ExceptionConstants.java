package az.cybernet.usermanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionConstants {
    USER_NOT_FOUND("USER_NOT_FOUND", "User with provided id was not found!"),
    VALIDATION_EXCEPTION("Validation_Exception", "Validation exception!");

    private final String code;
    private final String message;
}

