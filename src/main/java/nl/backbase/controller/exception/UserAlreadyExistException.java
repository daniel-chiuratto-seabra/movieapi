package nl.backbase.controller.exception;

import nl.backbase.repository.UserRepository;

/**
 * This {@link Exception} is thrown during the SignUp process, when the informed username already exists in the {@link UserRepository}
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(final String message) {
        super(message);
    }
}
