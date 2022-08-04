package nl.backbase.controller.advice;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler
    public ResponseEntity<?> movieSourceServiceException(final MovieAPISourceServiceException movieSourceServiceException) {
        log.error(movieSourceServiceException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieSourceServiceException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> movieSourceNotFoundException(final MovieAPISourceNotFoundException movieSourceNotFoundException) {
        log.warn("The movie {} has not been found", movieSourceNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.ofEntries(Map.entry("movieTitle", movieSourceNotFoundException.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<?> movieServiceFileLoadErrorException(final MovieAPPIServiceFileLoadErrorException movieServiceFileLoadErrorException) {
        final var message = String.format("An error occurred while loading the CSV file%n%s", movieServiceFileLoadErrorException.getMessage());
        log.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler
    public ResponseEntity<?> movieServiceAuthenticationException(final MovieAPIServiceAuthenticationException movieServiceAuthenticationException) {
        log.error(movieServiceAuthenticationException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieServiceAuthenticationException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidPasswordException(final InvalidPasswordException invalidPasswordException) {
        log.warn(invalidPasswordException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidPasswordException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> userAlreadyExistException(final UserAlreadyExistException userAlreadyExistException) {
        log.warn(userAlreadyExistException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.ofEntries(Map.entry("message",userAlreadyExistException.getMessage())));
    }
}
