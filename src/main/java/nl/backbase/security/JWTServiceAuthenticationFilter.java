package nl.backbase.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.JWTServiceAuthenticationException;
import nl.backbase.security.service.TokenAuthenticationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
public class JWTServiceAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) {
        try {
            final var authentication = this.tokenService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (final Exception e) {
            throw new JWTServiceAuthenticationException(e);
        }
    }
}
