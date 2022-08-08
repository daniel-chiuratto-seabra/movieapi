package nl.backbase.security.filter;

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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * This class is a {@link GenericFilterBean} implementation that has the goal to filter all the requests, looking for
 * the {@code JWT Token} and validating it (using the {@link TokenAuthenticationService}), where depending of how the
 * validation occurs, the flow tends to move forward, or a {@link HttpStatus#FORBIDDEN} status is returned to the user
 *
 * @author Daniel Chiuratto Seabra
 * @since 03/08/2022
 */
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends GenericFilterBean {

    private static final String ERROR_MESSAGE_FIELD = "errorMessage";

    private final TokenAuthenticationService tokenAuthenticationService;
    private final ObjectMapper objectMapper;

    /**
     * This method is called while Spring calls all the filters when a request is made, and it calls the
     * {@link TokenAuthenticationService#getAuthentication(HttpServletRequest)} to authenticate the corresponding request
     * in order to allow it to move forward or to forbid it
     *
     * @param request {@link ServletRequest} instance containing the request information
     * @param response {@link ServletResponse} instance to allow the server to respond the caller
     * @param filterChain {@link FilterChain} instance to allow the filter chain to move forward in case the authentication
     *                                       successfully happen
     * @throws IOException is thrown when an error happens when retrieving  {@link ServletResponse} content as {@link OutputStream}
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException {
        try {
            // First the authentication is retrieved by the TokenAuthenticationService, because, if it does return an Authentication
            // object, it means that the JWT token has been authenticated, if null, it means that it was not
            final var authentication = this.tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
            // The authentication is set into Spring's context, even if it is null, because furthermore in a next filter, having the
            // authentication as null, makes Spring to return a Forbidden status to the user
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Then it calls the next filter in the chain
            filterChain.doFilter(request, response);
        } catch (final MalformedJwtException malformedJwtException) {
            // When an invalid JWT Token is sent by the caller, a MalformedJwtException is thrown, therefore the user will
            // receive an Unauthorized status response and the request will not move forward
            log.error("Error during JWT authentication", malformedJwtException);
            writeErrorMessageResponse(response, malformedJwtException, HttpStatus.UNAUTHORIZED);
        } catch (final ServletException servletException) {
            // Then a ServletException may bw thrown either, and for that an Internal Server Error status will be returned
            // since that so far, for such scenario, it could be considered as an unpredicted error
            log.error("An error occurred during Servlet execution", servletException);
            writeErrorMessageResponse(response, servletException, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeErrorMessageResponse(final ServletResponse servletResponse, final Exception exception, final HttpStatus httpStatus) throws IOException {
        final var httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpResponse.setStatus(httpStatus.value());
        servletResponse.getOutputStream().write(this.objectMapper.writeValueAsString(Map.ofEntries(Map.entry(ERROR_MESSAGE_FIELD, exception.getMessage()))).getBytes(StandardCharsets.UTF_8));
    }
}
