package nl.backbase.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.dto.UserDTO;
import nl.backbase.security.JWTMovieAuthenticationManager;
import nl.backbase.security.service.TokenAuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Collections.emptyList;
import static nl.backbase.security.JWTConfigurationConstants.BEARER_TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * This {@link AbstractAuthenticationProcessingFilter} implementation class, has the goal to authenticate a given user
 * credentials that is trying to SignIn in the application. It authenticates the user utilising the {@link JWTMovieAuthenticationManager},
 * and once it happens successfully, it returns the {@code JWT Token} to the requester calling the {@link TokenAuthenticationService#buildJWTToken(String)}
 * through the {@link JWTSignInFilter#successfulAuthentication(HttpServletRequest, HttpServletResponse, FilterChain, Authentication)} method
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
public class JWTSignInFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final ObjectMapper objectMapper;

    public JWTSignInFilter(final String signInUrl, final AuthenticationManager authManager, final TokenAuthenticationService tokenAuthenticationService, final ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(signInUrl));
        setAuthenticationManager(authManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.objectMapper = objectMapper;
    }

    /**
     * This method is called everytime a user tries to SignIn into the application, and as its own name says, it attemps
     * to authenticate the credentials that it has been sent by the requester
     *
     * @param request {@link HttpServletRequest} instance containing the request information
     * @param response {@link HttpServletResponse} instance to allow to write a response in case is necessary
     * @return {@link Authentication} instance to move forward with the request flow
     * @throws AuthenticationException is thrown when the user credentials are not valid for some reason
     * @throws IOException is thrown when the payload deserialization coming from the {@link HttpServletRequest} cannot be
     * deserialized into {@link UserDTO}
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException {
        // First the UserDTO instance is retrieved and deserialized from the request, so the user credentials can be
        // retrieved and used in the authentication process
        final var userDTO = this.objectMapper.readValue(request.getInputStream(), UserDTO.class);
        // The JWTMovieAuthenticationManager is retrieved and has its authenticate method called to actually authenticate
        // the user credentials
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword(), emptyList()));
    }

    /**
     * This method is called once the authentication is made successfully,
     * therefore a {@code JWT Token} is generated and set in the {@link HttpHeaders#AUTHORIZATION} header to be sent bacl
     * to the caller to use it
     *
     * @param request {@link HttpServletRequest} instance containing the request information
     * @param response {@link HttpServletResponse} instance to allow settings in the response to the caller
     * @param filterChain {@link FilterChain} instance to allow access to the filter chain
     * @param authentication {@link Authentication} instance to allow access to the username of the caller to be used in the
     *                                             {@code JWT Token} generation
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @Override
    public void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain, final Authentication authentication) {
        // First call the JWT Token builder method by setting the username from the caller available in the Authentication
        // instance, to be set as a "Subject" claim for the JWT generation
        final var token = this.tokenAuthenticationService.buildJWTToken(authentication.getName());
        // Lastly the Authorization header is added in the response, with the token prefixed by the "Bearer" name
        response.addHeader(AUTHORIZATION, BEARER_TOKEN_PREFIX + " " + token);
    }
}