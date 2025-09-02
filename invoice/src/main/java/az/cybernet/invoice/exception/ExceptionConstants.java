package az.cybernet.invoice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum ExceptionConstants {
    UNEXPECTED_EXCEPTION("UNEXPECTED_EXCEPTION", "Unexpected exception occurred"),
    SENDER_NOT_FOUND("SENDER_NOT_FOUND", "no sender with id found"),
    INVOICE_NOT_FOUND("INVOICE_NOT_FOUND", "no invoice with id found"),
    MEASUREMENT_NOT_FOUND("MEASUREMENT_NOT_FOUND", "no measurent with id found"),
    VALIDATION_EXCEPTION("VALIDATION_EXCEPTION", "Validation exception"),
    INVALID_STATUS("INVALID_STATUS", "Only PENDING invoices can be ..."),
    UNAUTHORIZED("UNAUTHORIZED", "You are not allowed to perform surgery"),
    HTTP_METHOD_IS_NOT_CORRECT("HTTP_METHOD_IS_NOT_CORRECT", "http method is not correct"),
    RECIPIENT_NOT_FOUND("RECIPIENT_NOT_FOUND", "no recipient with id found"),
    ITEM_NOT_FOUND("ITEM_NOT_FOUND","item not found");

    String code;
    String message;

    public String getMessage(Long id) {
        if ((this == SENDER_NOT_FOUND || this == RECIPIENT_NOT_FOUND
                || this == INVOICE_NOT_FOUND) || this==MEASUREMENT_NOT_FOUND && id != null || this==ITEM_NOT_FOUND) {
            return String.format("No %s with id (ID: %s) was found",
                    this.name().toLowerCase().replace("_not_found", ""), id);
        }
        return this.message;
    }

    public String getMessage(String name) {
        if (this == MEASUREMENT_NOT_FOUND && name != null) {
            return String.format("No measurement with name (NAME: %s) was found", name);
        }
        return this.message;
    }
}
