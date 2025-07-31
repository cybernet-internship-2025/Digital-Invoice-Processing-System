package az.cybernet.usermanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleUserNotFound(UserNotFoundException ex) {
        return ExceptionResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
    }
    @ExceptionHandler(UserExists.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUserExists(UserExists ex) {
        return ExceptionResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
    }
}
