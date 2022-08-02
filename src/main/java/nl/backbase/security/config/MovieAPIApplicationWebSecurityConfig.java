package nl.backbase.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.security.JWTMovieApiApplicationAuthenticationFilter;
import nl.backbase.security.JWTMovieApiApplicationAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MovieAPIApplicationWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;
    private final String base64Secret;
    private final long tokenValidityInSeconds;

    public MovieAPIApplicationWebSecurityConfig(final UserDetailsService userDetailsService,
                                                final BCryptPasswordEncoder bCryptPasswordEncoder,
                                                final ObjectMapper objectMapper,
                                                @Value("${jwt.base64-secret}") final String base64Secret,
                                                @Value("${jwt.token-validity-in-seconds}") final long tokenValidityInSeconds) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.base64Secret = base64Secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
            http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, JWTMovieApiApplicationAuthenticationFilter.SIGNUP_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTMovieApiApplicationAuthenticationFilter(authenticationManager(), this.objectMapper, this.base64Secret, this.tokenValidityInSeconds))
                .addFilter(new JWTMovieApiApplicationAuthorizationFilter(authenticationManager(), this.base64Secret))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
    }
}
