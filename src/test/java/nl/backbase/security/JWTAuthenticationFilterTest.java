package nl.backbase.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import nl.backbase.security.filter.JWTAuthenticationFilter;
import nl.backbase.security.service.TokenAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {


    private nl.backbase.security.filter.JWTAuthenticationFilter JWTAuthenticationFilter;
    private TokenAuthenticationService mockTokenAuthenticationService;
    private ObjectMapper mockObjectMapper;

    @BeforeEach
    private void setUp() {
        this.mockTokenAuthenticationService = mock(TokenAuthenticationService.class);
        this.mockObjectMapper = mock(ObjectMapper.class);
        this.JWTAuthenticationFilter = new JWTAuthenticationFilter(this.mockTokenAuthenticationService, this.mockObjectMapper);
    }

    @Test
    @DisplayName("GIVEN a JWTAuthenticationFilter with mocked dependencies " +
                  "WHEN doFilter is called with mock values set " +
                   "AND the JWT token is processed successfully " +
                  "THEN the flow should move forward as expected")
    public void givenJWTAuthenticationFilterWhenDoFilterProcessedSuccessfullyThenFlowMoveForwardAsExpected() throws IOException, ServletException {
        final var mockServletRequest = mock(HttpServletRequest.class);
        final var mockServletResponse = mock(ServletResponse.class);
        final var mockFilterChain = mock(FilterChain.class);
        final var mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        final var mockAuthentication = mock(Authentication.class);

        when(this.mockTokenAuthenticationService.getAuthentication(eq(mockServletRequest))).thenReturn(mockAuthentication);

        this.JWTAuthenticationFilter.doFilter(mockServletRequest, mockServletResponse, mockFilterChain);

        verify(this.mockTokenAuthenticationService).getAuthentication(eq(mockServletRequest));
        verify(mockSecurityContext).setAuthentication(eq(mockAuthentication));
        verify(mockFilterChain).doFilter(eq(mockServletRequest), eq(mockServletResponse));
    }

    @Test
    @DisplayName("GIVEN a JWTAuthenticationFilter with mocked dependencies " +
                  "WHEN doFilter is called with mocked request, mocked response and mocked filter chain " +
                   "AND the TokenAuthenticationService throws a MalformedJwtException " +
                  "THEN the exception is catch and the response is built with the error message as expected and set in the response OutputStream")
    public void givenJWTAuthenticationFilterWhenDoFilterCalledThenMalformedJwtExceptionCatchAndResponseBuilt() throws IOException {
        final var mockServletRequest = mock(HttpServletRequest.class);
        final var mockServletResponse = mock(HttpServletResponse.class);
        final var mockServletOutputStream = mock(ServletOutputStream.class);

        final var expectedFakeExceptionMessage = "A fake exception message";

        final var mockFilterChain = mock(FilterChain.class);
        when(this.mockTokenAuthenticationService.getAuthentication(eq(mockServletRequest))).thenThrow(new MalformedJwtException(expectedFakeExceptionMessage));
        when(this.mockObjectMapper.writeValueAsString(any())).thenReturn(expectedFakeExceptionMessage);
        when(mockServletResponse.getOutputStream()).thenReturn(mockServletOutputStream);

        this.JWTAuthenticationFilter.doFilter(mockServletRequest, mockServletResponse, mockFilterChain);

        verify(this.mockObjectMapper).writeValueAsString(any());
        verify(mockServletOutputStream).write(any());
    }

    @Test
    @DisplayName("GIVEN a JWTAuthenticationFilter with mocked dependencies " +
                  "WHEN doFilter is called with mocked request, mocked response and mocked filter chain " +
                   "AND the TokenAuthenticationService throws a ServletException " +
                  "THEN the exception is catch and the response is built with the error message as expected and set in the response OutputStream")
    public void givenJWTAuthenticationFilterWhenDoFilterCalledThenServletExceptionCatchAndResponseBuilt() throws IOException, ServletException {
        final var mockAuthentication = mock(Authentication.class);
        final var mockServletRequest = mock(HttpServletRequest.class);
        final var mockServletResponse = mock(HttpServletResponse.class);
        final var mockServletOutputStream = mock(ServletOutputStream.class);

        final var expectedFakeExceptionMessage = "A fake exception message";

        final var mockFilterChain = mock(FilterChain.class);
        when(this.mockTokenAuthenticationService.getAuthentication(eq(mockServletRequest))).thenReturn(mockAuthentication);
        when(this.mockObjectMapper.writeValueAsString(any())).thenReturn(expectedFakeExceptionMessage);
        when(mockServletResponse.getOutputStream()).thenReturn(mockServletOutputStream);
        doThrow(new ServletException(expectedFakeExceptionMessage)).when(mockFilterChain).doFilter(eq(mockServletRequest), eq(mockServletResponse));

        this.JWTAuthenticationFilter.doFilter(mockServletRequest, mockServletResponse, mockFilterChain);

        verify(this.mockObjectMapper).writeValueAsString(any());
        verify(mockServletOutputStream).write(any());
    }
}