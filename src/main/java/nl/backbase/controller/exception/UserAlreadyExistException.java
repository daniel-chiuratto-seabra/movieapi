package nl.backbase.controller.exception;

import nl.backbase.repository.UserRepository;

/**
 * This {@link Exception} is thrown during the SignUp process, when the informed username already exists in the {@link UserRepository}
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
public class UserAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 2989616291432066581L;

	public UserAlreadyExistException(final String message) {
        super(message);
    }
}
