package nl.backbase.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.UserDTO;
import nl.backbase.repository.UserRepository;
import nl.backbase.security.JWTMovieAuthenticationManager;
import nl.backbase.security.filter.JWTAuthenticationFilter;
import nl.backbase.security.filter.JWTSignInFilter;
import nl.backbase.security.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is a {@link Configuration} class intended to configure the Web Security definitions regarding the SignIn and
 * SignUp processes, using JWT token for it
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class MovieApplicationWebSecurityConfig {

    private final String signUpUrl;
    private final String signInUrl;

    public MovieApplicationWebSecurityConfig(@Value("${security.jwt.url.signup}") final String signUpUrl,
                                             @Value("${security.jwt.url.signin}") final String signInUrl) {
        this.signInUrl = signInUrl;
        this.signUpUrl = signUpUrl;
    }

    /**
     * This method creates the {@link Bean} related to the {@link AbstractAuthenticationProcessingFilter} implementation,
     * which is the class where the filters used by Spring Security implements. For this specific filter that this
     * method instantiates, is where the {@code JWT Token} is generated once the authentication process happens successfully
     * on behalf of the {@link JWTMovieAuthenticationManager#authenticate(Authentication)} execution. Once the authentication
     * happens successfully, then the {@link JWTSignInFilter#successfulAuthentication(HttpServletRequest,
     * HttpServletResponse, FilterChain, Authentication)} returns the {@code JWT Token} to the user
     *
     * @param authManager {@link AuthenticationManager} instance to allow the {@link JWTSignInFilter} to authenticate the
     *                                                 SignIn request
     * @param tokenAuthenticationService {@link TokenAuthenticationService} instance to allow the {@link JWTSignInFilter}
     *                                                                     to generate the {@code JWT Token} once the
     *                                                                     authentication process happens successfully
     * @param objectMapper {@link ObjectMapper} instance to allow the {@link HttpServletRequest#getInputStream()} to be
     *                                         deserialized into {@link UserDTO} to be processed
     * @return {@link JWTSignInFilter} instance ready to filter SignIn requests
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    @Bean
    public JWTSignInFilter jwtSignInFilter(final AuthenticationManager authManager,
                                           final TokenAuthenticationService tokenAuthenticationService,
                                           final ObjectMapper objectMapper) {
        return new JWTSignInFilter(this.signInUrl, authManager, tokenAuthenticationService, objectMapper);
    }

    /**
     * This method creates the {@link Bean} related to the {@link JWTAuthenticationFilter}, which is one of the filters
     * implemented in this project to drive Spring Security to handle the requests. For this one, it is a filter that is
     * related in authenticating the user request by its {@code Token}, and it does that by using the
     * {@link TokenAuthenticationService#getAuthentication(HttpServletRequest)}, where if it returns a
     * {@link UsernamePasswordAuthenticationToken}, it moves forward with the flow because it means that the {@code JWT
     * Token} is valid, otherwise it returns {@code null}, which for Spring Security means that the user that made the
     * request is not authenticated and then a {@link org.springframework.http.HttpStatus#FORBIDDEN} is replied to the
     * user.
     *
     * @param tokenAuthenticationService {@link TokenAuthenticationService} instance, used to authenticate the request
     *                                                                     (or not)
     * @param objectMapper {@link ObjectMapper} instance used to serialize the error message in case it happens so the
     *                                         user can have a user-friendly response
     * @return {@link JWTAuthenticationFilter} instance to be available in the Spring context, so it can be part of the
     * Spring filters in order to analyse if a given request is authenticated or not
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(final TokenAuthenticationService tokenAuthenticationService,
                                                                  final ObjectMapper objectMapper) {
        return new JWTAuthenticationFilter(tokenAuthenticationService, objectMapper);
    }

    /**
     * This method creates the {@link JWTMovieAuthenticationManager} which is an implementation of {@link AuthenticationManager},
     * and is responsible in actually authenticate (or not) a user that is requesting to SignIn. It is this class that
     * during SignIn, verifies if the informed username exists in the database, and if so, if its password matches with
     * what is available in the database
     *
     * @param userRepository {@link UserRepository} instance to allow the {@link AuthenticationManager} to access the
     *                                             {@link UserRepository} to verify if the username that is requesting to
     *                                             SignIn exists in the database (if it Signed Up before Signing In)
     * @param passwordEncoder {@link PasswordEncoder} instance containing the implementation that verifies if the
     *                                               user that is requesting to SignIn contains a password that matches
     *                                               with the one available in the database, and since the password is
     *                                               stored encoded, only the {@link PasswordEncoder} is able to verify
     *                                               if the given password matches with the one available in the database
     * @return {@link AuthenticationManager} instance to be injected into the {@link JWTSignInFilter}, since it is the
     * class that verifies if the given credentials are correct to be authenticated
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    @Bean
    public AuthenticationManager authenticationManager(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        return new JWTMovieAuthenticationManager(userRepository, passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http,
                                           final JWTSignInFilter jwtSignInFilter,
                                           final JWTAuthenticationFilter authenticationFilter) throws Exception {
        return http.csrf().disable()
                    .authorizeRequests()

                    // Allows SignUp and SignIn urls
                    .antMatchers(HttpMethod.POST, this.signInUrl).permitAll()
                    .antMatchers(HttpMethod.POST, this.signUpUrl).permitAll()

                    // Swagger is permitted here, but it is configured to try to access the
                    // API only with a JWT Token set
                    .antMatchers("/swagger-ui/**").permitAll()
                    .antMatchers("/v3/api-docs/**").permitAll()

                    // H2 Console is allowed here to be accessible in DEV environments but in PROD the H2 Console
                    // is not enabled
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/favicon.ico").permitAll()

                    // Requires Authentication
                    .anyRequest().authenticated()

                    // In order to allow H2 Console access
                    .and()
                    .headers().frameOptions().sameOrigin()

                    // Session Configuration
                    .and()
                    // Stateless since we are using JWT Token, and a JSESSIONID is not needed
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    // The filters that work with SignIn and request authentication flows
                    .addFilterBefore(jwtSignInFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
}
