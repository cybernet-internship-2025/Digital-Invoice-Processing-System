package az.cybernet.invoice.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotFoundException extends RuntimeException {
    String code;

    public NotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
}
