package nl.backbase.controller.exception;

import nl.backbase.repository.UserRepository;

/**
 * This {@link Exception} represents an authentication validation result when the informed password does not match
 * with the one saved in the {@link UserRepository}
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(final String message) {
        super(message);
    }
}
