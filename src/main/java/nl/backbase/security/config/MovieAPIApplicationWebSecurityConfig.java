package nl.backbase.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.repository.UserRepository;
import nl.backbase.security.JWTServiceAuthenticationFilter;
import nl.backbase.security.JWTSignUpFilter;
import nl.backbase.security.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true) // allows @Secured annotation
public class MovieAPIApplicationWebSecurityConfig {

    private final String signUpUrl;
    private final String signInUrl;

    public MovieAPIApplicationWebSecurityConfig(@Value("${security.jwt.url.signup}") final String signUpUrl,
                                                @Value("${security.jwt.url.signin}") final String signInUrl) {
        this.signInUrl = signInUrl;
        this.signUpUrl = signUpUrl;
    }

    @Bean
    public JWTSignUpFilter jwtSignUpFilter(final AuthenticationManager authManager,
                                           final TokenAuthenticationService tokenAuthenticationService,
                                           final ObjectMapper objectMapper) {
        return new JWTSignUpFilter(this.signInUrl, authManager, tokenAuthenticationService, objectMapper);
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return new TokenAuthenticationService();
    }

    @Bean
    public JWTServiceAuthenticationFilter jwtServiceAuthenticationFilter(final TokenAuthenticationService tokenAuthenticationService,
                                                                         final ObjectMapper objectMapper) {
        return new JWTServiceAuthenticationFilter(tokenAuthenticationService, objectMapper);
    }

    @Bean
    public AuthenticationManager authenticationManager(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        return new JWTMovieAPIAuthenticationManager(userRepository, passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http,
                                           final JWTSignUpFilter loginFilter,
                                           final JWTServiceAuthenticationFilter authenticationFilter) throws Exception {
        return http.csrf().disable()
                    .authorizeRequests()

                    // Allows SignUp and SignIn urls
                    .antMatchers(HttpMethod.POST, this.signInUrl).permitAll()
                    .antMatchers(HttpMethod.POST, this.signUpUrl).permitAll()

                    // Swagger is permitted here, but it is configured to try to access the
                    // API only with a JWT Token set
                    .antMatchers("/swagger-ui/**").permitAll()
                    .antMatchers("/v3/api-docs/**").permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/favicon.ico").permitAll()

                    // Requires Authentication
                    .anyRequest().authenticated()

                    // In order to allow the H2 Console access
                    .and().headers().frameOptions().sameOrigin()

                    // Session
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
}
