package nl.backbase.controller.advice;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * 
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MovieAPISourceServiceException.class)
    public ResponseEntity<?> movieSourceServiceException(final MovieAPISourceServiceException movieSourceServiceException) {
        log.error(movieSourceServiceException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieSourceServiceException.getMessage());
    }

    @ExceptionHandler(MovieAPINotFoundException.class)
    public ResponseEntity<?> movieSourceNotFoundException(final MovieAPINotFoundException movieNotFoundException) {
        log.warn("The movie {} has not been found", movieNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ofEntries(entry("movieTitle", movieNotFoundException.getMessage())));
    }

    @ExceptionHandler(MovieAPPIServiceFileLoadErrorException.class)
    public ResponseEntity<?> movieServiceFileLoadErrorException(final MovieAPPIServiceFileLoadErrorException movieServiceFileLoadErrorException) {
        final var message = String.format("An error occurred while loading the CSV file%n%s", movieServiceFileLoadErrorException.getMessage());
        log.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler(MovieAPIServiceAuthenticationException.class)
    public ResponseEntity<?> movieServiceAuthenticationException(final MovieAPIServiceAuthenticationException movieServiceAuthenticationException) {
        log.error(movieServiceAuthenticationException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieServiceAuthenticationException.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPasswordException(final InvalidPasswordException invalidPasswordException) {
        log.warn(invalidPasswordException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidPasswordException.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> userAlreadyExistException(final UserAlreadyExistException userAlreadyExistException) {
        log.warn(userAlreadyExistException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofEntries(entry("message", userAlreadyExistException.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(final MethodArgumentNotValidException methodArgumentNotValidException) {
        final var allErrorsList = methodArgumentNotValidException.getAllErrors();
        final var errorMessage = allErrorsList.stream().map(objectError -> String.format("%s: %s", Objects.requireNonNull(objectError.getCodes())[0], objectError.getDefaultMessage())).collect(Collectors.joining(", "));
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofEntries(entry("message", errorMessage)));
    }
}
