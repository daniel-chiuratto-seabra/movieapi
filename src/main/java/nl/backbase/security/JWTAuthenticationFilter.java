package nl.backbase.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.security.service.TokenAuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends GenericFilterBean {

    public static final String ERROR_MESSAGE_FIELD = "errorMessage";

    private final TokenAuthenticationService tokenAuthenticationService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException {
        try {
            final var authentication = this.tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (final MalformedJwtException malformedJwtException) {
            log.error("Error during JWT authentication", malformedJwtException);

            final var httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.getOutputStream().write(this.objectMapper.writeValueAsString(Map.ofEntries(Map.entry(ERROR_MESSAGE_FIELD, malformedJwtException.getMessage()))).getBytes(StandardCharsets.UTF_8));
        } catch (final ServletException servletException) {
            log.error("An error occurred during Servlet execution", servletException);

            final var httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

            response.getOutputStream().write(this.objectMapper.writeValueAsString(Map.ofEntries(Map.entry(ERROR_MESSAGE_FIELD, servletException.getMessage()))).getBytes(StandardCharsets.UTF_8));
        }
    }
}
