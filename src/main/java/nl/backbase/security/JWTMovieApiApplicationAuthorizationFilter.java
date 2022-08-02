package nl.backbase.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Collections;

public class JWTMovieApiApplicationAuthorizationFilter extends BasicAuthenticationFilter {
    public static final String BEARER_HEADER = "Bearer ";
    private final Key key;

    public JWTMovieApiApplicationAuthorizationFilter(final AuthenticationManager authenticationManager, final String base64Secret) {
        super(authenticationManager);
        this.key = getKey(base64Secret);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith(BEARER_HEADER)) {
            chain.doFilter(request, response);
            return;
        }

        final var authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final String token) {
        final var claims = Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody();
        final var user = new User(claims.getSubject(), "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(user, "", Collections.emptyList());
    }

    private SecretKey getKey(final String base64Secret) {
        final var keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
