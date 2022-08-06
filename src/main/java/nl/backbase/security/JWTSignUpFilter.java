package nl.backbase.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.dto.UserDTO;
import nl.backbase.security.service.TokenAuthenticationService;
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
import static nl.backbase.security.JWTConfigurationConstants.AUTHORIZATION_HEADER_STRING;
import static nl.backbase.security.JWTConfigurationConstants.TOKEN_PREFIX;

public class JWTSignUpFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final ObjectMapper objectMapper;

    public JWTSignUpFilter(final String signUpUrl, final AuthenticationManager authManager, final TokenAuthenticationService tokenAuthenticationService, final ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(signUpUrl));
        setAuthenticationManager(authManager);
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException {
        final var userDTO = this.objectMapper.readValue(request.getInputStream(), UserDTO.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword(), emptyList()));
    }

    @Override
    public void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain, final Authentication authentication) {
        final var token = this.tokenAuthenticationService.buildJWTToken(authentication.getName());
        response.addHeader(AUTHORIZATION_HEADER_STRING, TOKEN_PREFIX + " " + token);
    }
}