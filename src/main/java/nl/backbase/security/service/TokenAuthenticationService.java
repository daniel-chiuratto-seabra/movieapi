package nl.backbase.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static nl.backbase.security.JWTConfigurationConstants.HEADER_STRING;
import static nl.backbase.security.JWTConfigurationConstants.TOKEN_PREFIX;

public class TokenAuthenticationService {

    @Value("${security.jwt.token.expiration}")
    private Long expirationTime;

    @Value("${security.jwt.token.secret}")
    private String secret;

    // Used to build a JWT Token for a new login
    public String buildJWTToken(final String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(currentTimeMillis() + this.expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(final HttpServletRequest request) {
        final var token = request.getHeader(HEADER_STRING);
        if (token != null) {
            final var user = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                                        .parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
                                        .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, emptyList());
            }
        }
        return null;
    }
}