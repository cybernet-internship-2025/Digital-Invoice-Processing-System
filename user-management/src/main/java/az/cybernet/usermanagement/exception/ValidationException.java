package az.cybernet.usermanagement.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = PRIVATE)
public class ValidationException {
    String field;
    String message;
}