package nl.backbase.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.dto.UserDTO;
import nl.backbase.security.filter.JWTSignInFilter;
import nl.backbase.security.service.TokenAuthenticationService;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static nl.backbase.security.JWTConfigurationConstants.BEARER_TOKEN_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

class JWTSignUpFilterTest {

    private JWTMovieAuthenticationManager mockAuthenticationManager;
    private TokenAuthenticationService mockTokenAuthenticationService;
    private ObjectMapper mockObjectMapper;
    private JWTSignInFilter jwtSignUpFilter;

    @BeforeEach
    private void setUp() {
        this.mockAuthenticationManager = mock(JWTMovieAuthenticationManager.class);
        this.mockTokenAuthenticationService = mock(TokenAuthenticationService.class);
        this.mockObjectMapper = mock(ObjectMapper.class);
        this.jwtSignUpFilter = new JWTSignInFilter("Fake Url", mockAuthenticationManager, mockTokenAuthenticationService, mockObjectMapper);
    }

    @Test
    @DisplayName("GIVEN a JWTSignInFilter with mocked and fake dependencies " +
                  "WHEN attemptAuthentication is called with the fake request and response set " +
                  "THEN the authentication manager should call the authentication manager to authenticate the user")
    public void givenJWTSignUpFilterWhenAttemptAuthenticationCalledThenAuthenticationManagerShouldCallAuthenticationManagerToAuthenticateUser() throws IOException {
        final var fakeRequest = mock(HttpServletRequest.class);
        final var fakeResponse = mock(HttpServletResponse.class);
        final var mockServletInputStream = mock(ServletInputStream.class);
        final var expectedFakeUserDTO = new UserDTO();

        when(fakeRequest.getInputStream()).thenReturn(mockServletInputStream);
        when(this.mockObjectMapper.readValue(eq(mockServletInputStream), eq(UserDTO.class))).thenReturn(expectedFakeUserDTO);

        this.jwtSignUpFilter.attemptAuthentication(fakeRequest, fakeResponse);

        verify(this.mockAuthenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("GIVEN a JWTSignInFilter with mocked and fake dependencies " +
                  "WHEN successfulAuthentication is called with the fake request, response, filter chain and authentication set " +
                  "THEN the TokenAuthenticationService builds the JWT Token and sets it in the response Authorization header")
    public void givenJWTSignUpFilterWhenSuccessfulAuthenticationCalledThenTokenAuthenticationServiceBuildsJWTTokenAndSetsItInResponseHeader() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakePassword = "Fake Password";
        final var expectedFakeToken = "Fake Token";
        final var mockHttpServletRequest = mock(HttpServletRequest.class);
        final var httpServletResponse = new Response();
        httpServletResponse.setCoyoteResponse(new org.apache.coyote.Response());

        final var mockFilterChain = mock(FilterChain.class);
        final var expectedFakeAuthentication = new UsernamePasswordAuthenticationToken(expectedFakeUsername, expectedFakePassword, Collections.emptyList());

        when(mockTokenAuthenticationService.buildJWTToken(eq(expectedFakeUsername))).thenReturn(expectedFakeToken);

        this.jwtSignUpFilter.successfulAuthentication(mockHttpServletRequest, httpServletResponse, mockFilterChain, expectedFakeAuthentication);

        final var actualToken = httpServletResponse.getHeader(AUTHORIZATION);
        assertEquals(BEARER_TOKEN_PREFIX + " " + expectedFakeToken, actualToken);
    }
}
