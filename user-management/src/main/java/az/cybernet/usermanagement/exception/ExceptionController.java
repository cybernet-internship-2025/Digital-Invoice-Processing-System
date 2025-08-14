package az.cybernet.usermanagement.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static az.cybernet.usermanagement.exception.ExceptionConstants.INVALID_TAX_ID_EXCEPTION;
import static az.cybernet.usermanagement.exception.ExceptionConstants.VALIDATION_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ExceptionResponse handleUserNotFound(NotFoundException ex) {
        log.error("NotFoundException", ex);
        return ExceptionResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);
        List<ValidationException> exceptions = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationException(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ExceptionResponse.builder()
                .code(VALIDATION_EXCEPTION.getCode())
                .message(VALIDATION_EXCEPTION.getMessage())
                .validationErrors(exceptions)
                .build();
    }

    @ExceptionHandler(InvalidTaxIdException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleInvalidTaxIdException(InvalidTaxIdException ex) {
        log.error("InvalidTaxIdException", ex);
        return ExceptionResponse.builder()
                .code(INVALID_TAX_ID_EXCEPTION.getCode())
                .message(INVALID_TAX_ID_EXCEPTION.getMessage())
                .build();
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleUnhandledExceptions(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ExceptionResponse.builder()
                .code("INTERNAL_ERROR")
                .message(ex.getMessage())
                .build();
    }

}
