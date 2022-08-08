package nl.backbase.controller.advice;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * This class is the Exception Handler of the entire application, handling all the predicted {@link Exception}s that
 * may be thrown, logging them and parsing (when needed) its message to a more user-friendly way.
 *
 * @author Daniel Chiuratto Seabra
 * @since 01/08/2022
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    /**
     * This handler handles a {@link MovieSourceServiceException}, that is an {@link Exception} that represents an
     * issue that may occur while the {@link RestTemplate} has issues in requesting data to the external Movie API Source
     *
     * @param movieSourceServiceException {@link MovieSourceServiceException} instance containing the information about the issue
     * @return {@link ResponseEntity} with the parsed error data regarding the connection with the Movie service
     *
     * @author Daniel Chiuratto Seabra
     * @since 01/08/2022
     */
    @ExceptionHandler(MovieSourceServiceException.class)
    public ResponseEntity<?> movieSourceServiceException(final MovieSourceServiceException movieSourceServiceException) {
        log.error(movieSourceServiceException.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(movieSourceServiceException.getMessage());
    }

    /**
     * This handler handles a {@link MovieNotFoundException}, that is an {@link Exception} thrown when a requested
     * Movie Title does not won a Best Picture Oscar, and also when the requested Movie Title is not found in the external
     * Movie API Service in flows that does such request.
     *
     * @param movieNotFoundException {@link MovieNotFoundException} instance containing the information regarding the Movie
     * @return {@link ResponseEntity} with the parsed error data regarding the Movie Title that were not found
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<?> movieSourceNotFoundException(final MovieNotFoundException movieNotFoundException) {
        log.warn("The movie {} has not been found", movieNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ofEntries(entry("movieTitle", movieNotFoundException.getMessage())));
    }

    /**
     * This handler handles a {@link InvalidPasswordException}, that is an {@link Exception} thrown during the authentication
     * phase, where when the password does not match with the one saved in the database, this {@link Exception} is thrown
     * returning an {@code UNAUTHORIZED} response to the user.
     *
     * @param invalidPasswordException {@link InvalidPasswordException} instance containing the information regarding the password
     * @return {@link ResponseEntity} with the parsed error data regarding the wrong password
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPasswordException(final InvalidPasswordException invalidPasswordException) {
        log.warn(invalidPasswordException.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidPasswordException.getMessage());
    }

    /**
     * This handler handles an {@link UserAlreadyExistException}, that is an {@link Exception} thrown during the SignUp process,
     * and is thrown when the username already exists in the database.
     *
     * @param userAlreadyExistException {@link UserAlreadyExistException} instance containing the information regarding the user that exists already
     * @return {@link ResponseEntity} with the parsed error data regarding the user that already exists in the system
     *
     * @author Daniel Chiuratto Seabra
     * @since 04/08/2022
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> userAlreadyExistException(final UserAlreadyExistException userAlreadyExistException) {
        log.warn(userAlreadyExistException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofEntries(entry("message", userAlreadyExistException.getMessage())));
    }

    /**
     * This handler handlers a {@link MethodArgumentNotValidException}, that is an {@link Exception} thrown by the {@code Hibernate Validator},
     * in the validators set in the Data Transfer Objects (DTO), and is intended to make the response more user-friendly since the default
     * handler from Spring returns a very "dirty" message
     *
     * @param methodArgumentNotValidException {@link MethodArgumentNotValidException} instance containing the data regarding the validation result
     * @return {@link ResponseEntity} with the parsed error data regarding the field and what happened that it has been rejected by the validator
     *
     * @author Daniel Chiuratto Seabra
     * @since 07/08/2022
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(final MethodArgumentNotValidException methodArgumentNotValidException) {
        final var allErrorsList = methodArgumentNotValidException.getAllErrors();
        final var errorMessage = allErrorsList.stream().map(objectError -> String.format("%s: %s", Objects.requireNonNull(objectError.getCodes())[0], objectError.getDefaultMessage())).collect(Collectors.joining(", "));
        log.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofEntries(entry("message", errorMessage)));
    }
}
