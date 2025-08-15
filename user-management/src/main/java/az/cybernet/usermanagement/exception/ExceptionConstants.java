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

    public String getMessage(String taxId) {
        if ((this == USER_NOT_FOUND)) {
            return String.format("No %s with id (ID: %s) was found",
                    this.name().toLowerCase().replace("_not_found", ""), taxId);
        }
        return this.message;
    }
}

