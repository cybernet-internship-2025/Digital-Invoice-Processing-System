package az.cybernet.invoice.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomFeignException extends RuntimeException {
    String code;
    int status;

    public CustomFeignException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}