package az.cybernet.usermanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionConstants {
    USER_NOT_FOUND("001", "User not found!");

    private final String code;
    private final String message;

    public String getCode() { return code; }
    public String getMessage() { return message; }
}

