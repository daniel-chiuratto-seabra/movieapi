package nl.backbase.controller.advice;

import nl.backbase.controller.exception.MovieServiceFileLoadErrorException;
import nl.backbase.controller.exception.MovieSourceNotFoundException;
import nl.backbase.controller.exception.MovieSourceServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler
    public ResponseEntity<?> movieSourceServiceException(final MovieSourceServiceException movieSourceServiceException) {
        LOGGER.error(movieSourceServiceException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieSourceServiceException.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> movieSourceNotFoundException(final MovieSourceNotFoundException movieSourceNotFoundException) {
        LOGGER.warn("The movie {} has not been found", movieSourceNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.ofEntries(Map.entry("movieTitle", movieSourceNotFoundException.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<?> movieServiceFileLoadErrorException(final MovieServiceFileLoadErrorException movieServiceFileLoadErrorException) {
        final var message = String.format("An error occurred while loading the CSV file%n%s", movieServiceFileLoadErrorException.getMessage());
        LOGGER.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}
