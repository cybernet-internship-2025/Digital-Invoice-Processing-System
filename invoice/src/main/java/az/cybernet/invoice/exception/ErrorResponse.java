package az.cybernet.invoice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ErrorResponse {
    String code;
    String message;
    List<ValidationException> validationExceptions;
}
