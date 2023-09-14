package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Ошибка валидации - {}: {}", fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final RuntimeException ex) {
        log.debug("Получен статус 404 Not found {}", ex.getMessage(), ex);
        return createErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AlreadyExistException.class, NotAvailableException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictExceptions(final RuntimeException ex) {
        log.debug("Получен статус 409 Conflict {}", ex.getMessage(), ex);
        return createErrorResponse(ex, HttpStatus.CONFLICT);
    }

    /*@ExceptionHandler(IncorrectParameter.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectParameterExceptions(final RuntimeException ex) {
        log.debug("Получен статус 400 Bad request {}", ex.getMessage(), ex);
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleConstraintViolation(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        List<String> causes = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        log.debug("Получен статус 400 {} Bad request", ex.getMessage(), ex);
        return new ApiError(String.join(", ", messages),
                String.join(", ", causes),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable ex) {
        log.debug("Внутренняя ошибка сервера 500 Internal Server Error {}", ex.getMessage(), ex);
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ApiError createErrorResponse(Throwable ex, HttpStatus httpStatus) {
        return new ApiError(ex.getMessage(),
                String.valueOf(ex.getCause()),
                httpStatus.name(),
                LocalDateTime.now());
    }
}
