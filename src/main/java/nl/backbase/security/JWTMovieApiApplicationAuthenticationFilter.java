package nl.backbase.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieServiceAuthenticationException;
import nl.backbase.dto.UserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

public class JWTMovieApiApplicationAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SIGNUP_URL = "/v1/users";

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final long tokenValidityInMilliseconds;
    private final Key key;

    public JWTMovieApiApplicationAuthenticationFilter(final AuthenticationManager authenticationManager, final ObjectMapper objectMapper,
                                                      final String base64Secret, final long tokenValidityInSeconds) {
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000L;
        this.key = getKey(base64Secret);
        this.setFilterProcessesUrl(SIGNUP_URL);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        try {
            final var userDTO = this.objectMapper.readValue(request.getInputStream(), UserDTO.class);
            return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword(), Collections.emptyList()));
        } catch (final IOException e) {
            throw new MovieServiceAuthenticationException(e);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException {
        final var userDTO = (UserDTO) authResult.getPrincipal();
        final var token = Jwts.builder().setSubject(userDTO.getUsername())
                                                   .signWith(this.key, SignatureAlgorithm.PS512)
                .setExpiration(new Date(System.currentTimeMillis() + this.tokenValidityInMilliseconds))
                .compact();
        final var body = userDTO.getUsername() + " " + token;
        response.getWriter().write(body);
        response.getWriter().flush();
    }

    private SecretKey getKey(final String base64Secret) {
        final var keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
