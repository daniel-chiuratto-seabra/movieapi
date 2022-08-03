package nl.backbase.security.config;

import lombok.RequiredArgsConstructor;
import nl.backbase.controller.exception.InvalidPasswordException;
import nl.backbase.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@RequiredArgsConstructor
public class JWTMovieAPIAuthenticationManager implements AuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final var principal = String.valueOf(authentication.getPrincipal());
        final var usernameEntity = this.userRepository.findByUsername(principal);
        if (usernameEntity == null) {
            throw new UsernameNotFoundException(String.format("The user name '%s' has not been found", principal));
        }
        if (!this.passwordEncoder.matches(String.valueOf(authentication.getCredentials()), usernameEntity.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(usernameEntity.getUsername(), usernameEntity.getPassword(), Collections.emptyList());
    }
}
