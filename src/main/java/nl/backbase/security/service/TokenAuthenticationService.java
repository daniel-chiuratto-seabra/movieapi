package nl.backbase.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static nl.backbase.security.JWTConfigurationConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class TokenAuthenticationService {

    private final Long expirationTime;
    private final String secret;

    // Used to build a JWT Token for a new login
    public String buildJWTToken(final String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(currentTimeMillis() + this.expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Authentication getAuthentication(final HttpServletRequest request) throws MalformedJwtException {
        final var token = request.getHeader(AUTHORIZATION);
        if (token != null) {
            final var user = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                                        .parseClaimsJws(token.replace(TOKEN_PREFIX, StringUtils.EMPTY)).getBody()
                                        .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, emptyList());
            }
        }
        return null;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}