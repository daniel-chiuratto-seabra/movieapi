package nl.backbase.security;

import lombok.RequiredArgsConstructor;
import nl.backbase.controller.exception.InvalidPasswordException;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import nl.backbase.security.config.MovieApplicationWebSecurityConfig;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

/**
 * This class is an implementation of the {@link AuthenticationManager}, where it is the class used to authenticate the
 * request in order to see if the credentials sent by the user is available in the database, and if the username returns
 * a {@link UserEntity} and the password stored in the database matches with the one sent in the request, it means that
 * the requester can be authenticated, and receive a JWT token, otherwise if the user is not available in the database,
 * then a {@link UsernameNotFoundException} is thrown, and if the user exists in the database but the provided password
 * does not match with the one available in the database, then a {@link InvalidPasswordException} is thrown.
 * <br /><br />
 * This class is instantiated in the {@link MovieApplicationWebSecurityConfig#authenticationManager(UserRepository, PasswordEncoder)}
 * method, making it available in the Spring context so Spring Context can use it as an authenticator during the SignIn process.
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
@RequiredArgsConstructor
public class JWTMovieAuthenticationManager implements AuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This method is called everytime a user tries to SignIn, informing its credentials. So, in this method the username
     * and its password is verified with the one available in the database.
     * <br /><ul><li>
     *      In case the provided username is not available in the database, then a {@link UsernameNotFoundException} is thrown,
     * returning a {@link HttpStatus#NOT_FOUND} status to the user
     *  </li>
     *  <li>
     *      In case the provided username exists in the database, but its password does not match with the one available in the
     *      database, then a {@link HttpStatus#UNAUTHORIZED} status is returned to the user
     *  </li></ul>
     *  <br />
     *
     * @param authentication {@link Authentication} instance containing the credential information from the user that is
     *                                             trying to SignIn
     * @return {@link Authentication} instance with the credentials that means that the user is authenticated and allowed
     * to use the application
     * @throws AuthenticationException when an {@link AuthenticationException} is thrown then an {@link ExceptionHandler}
     * handles it in order to return the proper response to the user
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        // Principal is retrieved from the Authentication object and parsed to String because there is no way to
        // such data become with something different from String
        final var principal = String.valueOf(authentication.getPrincipal());

        // Using the principal (that actually is the username), the repository is called in order to see if there is an
        // user already in the database, which means that is a user that SignedUp already
        final var usernameEntity = this.userRepository.findByUsername(principal);
        // If the usernameEntity returns null, it means that the user did not SignedUp, therefore a UsernameNotFoundException
        // should be thrown
        if (usernameEntity == null) {
            throw new UsernameNotFoundException(String.format("The user name '%s' has not been found", principal));
        }
        // If the user exists in the database, then its password should be verified, and this needs to be made with the
        // Password Encoder, since the password is encoded before stored in the database, if it does not match, then an
        // InvalidPasswordException is thrown
        if (!this.passwordEncoder.matches(String.valueOf(authentication.getCredentials()), usernameEntity.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        // If none of the Exceptions above is thrown, it means that the user is really himself, so the flow should move forward
        return new UsernamePasswordAuthenticationToken(usernameEntity.getUsername(), usernameEntity.getPassword(), Collections.emptyList());
    }
}
