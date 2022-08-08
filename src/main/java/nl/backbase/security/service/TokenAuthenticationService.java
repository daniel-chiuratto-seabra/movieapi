package nl.backbase.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.backbase.security.filter.JWTAuthenticationFilter;
import nl.backbase.security.filter.JWTSignInFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static nl.backbase.security.JWTConfigurationConstants.BEARER_TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


/**
 * This {@link Service} class has the objective of generating and validating the {@code JWT Token}, being consumed by
 * both {@link JWTAuthenticationFilter} and {@link JWTSignInFilter} filters, that are responsible to filter the
 * requests using the {@code JWT} method.
 * <br /><br />
 * It is in this {@link Service} where both properties are set:
 * <br />
 * <ul>
 *     <li><b>security.jwt.token.expiration:</b> Sets the amount of time until the {@code Token} gets expired (in
 *     milliseconds)</li>
 *     <li><b>security.jwt.token.secret:</b> Sets the secret used by the {@code JWT Token} generator</li>
 * </ul>
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
@Service
public class TokenAuthenticationService {

    private final Long expiration;
    private final String secret;

    public TokenAuthenticationService(@Value("${security.jwt.token.expiration}") final Long expiration,
                                      @Value("${security.jwt.token.secret}") final String secret) {
        this.expiration = expiration;
        this.secret = secret;
    }

    /**
     * This method is the one which actually generates the {@code JWT Token}, setting the Subject {@code claim} and
     * defining the expiration time that is the sum of current time with the value specified in milliseconds by the
     * property {@code security.jwt.token.expiration}
     *
     * @param subject {@link String} representing the subject to be used in the JWT Token payload
     * @return {@link String} representing the generated {@code JWT Token}
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    public String buildJWTToken(final String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(currentTimeMillis() + this.expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * This method is called by the {@link JWTAuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * because it is the one responsible in authenticating the {@code JWT Token} sent by the User everytime it does a
     * request to the service. If the authentication does not return an {@link Authentication} instance, Spring Security
     * will consider the request as a {@link HttpStatus#FORBIDDEN} and will return that to the user
     *
     * @param request {@link HttpServletRequest} instance with the request data to be authenticated
     * @return {@link Authentication} instance ({@link UsernamePasswordAuthenticationToken} specifically), containing the
     * authenticated credentials
     * @throws MalformedJwtException thrown when an invalid {@code JWT Token} is sent in the request
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    public Authentication getAuthentication(final HttpServletRequest request) throws MalformedJwtException {
        // First the token is retrieved from the request from the Authorization header
        final var token = request.getHeader(AUTHORIZATION);
        // The token is validated only if it is non-null, naturally
        if (token != null) {
            // The JWT parser then uses the singing key which comes from the given secret and parses the JWT Token
            // retrieving the Subject, which basically is the username that is currently logged in, where if it is
            // non-null, it means that is everything fine, and it is an authenticated user, otherwise, it returns null,
            // showing to Spring Boot that the user is forbidden, or it will throw a MalformedJwtException, that means
            // an invalid JWT Token, forbidding the request as well
            final var user = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                                        .parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, StringUtils.EMPTY)).getBody()
                                        .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, emptyList());
            }
        }
        return null;
    }

    /**
     * This method is used to decode the secret that has been set by the property {@code security.jwt.token.secret} in
     * Base 64 encoding, and then it generates a {@link SecretKey} instance with HMAC-SHA algorithm to generate the
     * {@code JWT TOken}
     *
     * @return {@link Key} instance (which actually is a {@link SecretKey} instance) to use in the {@code JWT Token}
     *
     * @author Daniel Chiuratto Seabra
     * @since 03/08/2022
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}