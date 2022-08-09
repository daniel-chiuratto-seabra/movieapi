package nl.backbase.controller.exception;

import nl.backbase.repository.UserRepository;
import org.springframework.security.core.AuthenticationException;

/**
 * This {@link Exception} represents an authentication validation result when the informed password does not match
 * with the one saved in the {@link UserRepository}
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
public class InvalidPasswordException extends AuthenticationException {
    private static final long serialVersionUID = -7827967576484935729L;

	public InvalidPasswordException(final String message) {
        super(message);
    }
}
