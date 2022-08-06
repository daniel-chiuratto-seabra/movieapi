package nl.backbase.security.service;

import io.jsonwebtoken.io.Encoders;
import nl.backbase.security.JWTConfigurationConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TokenAuthenticationServiceTest {

    @Test
    @DisplayName("GIVEN a TokenAuthenticationService with fake values " +
                  "WHEN getAuthentication is called with a mocked HttpServletRequest that returns null as Authorization header value " +
                  "THEN the TokenAuthenticationService should return a null authentication")
    public void givenTokenAuthenticationServiceWhenGetAuthenticationCalledThenTokenAuthenticationServiceReturnNullAuthentication() {
        final var mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        final var tokenAuthenticationService = new TokenAuthenticationService(System.currentTimeMillis(), StringUtils.EMPTY);
        final var actualAuthentication = tokenAuthenticationService.getAuthentication(mockHttpServletRequest);
        assertNull(actualAuthentication);
    }

    @Test
    @DisplayName("GIVEN a TokenAuthenticationService with fake values " +
                  "WHEN getAuthentication is called with a mocked HttpServletRequest that returns a fake token as Authorization header value " +
                  "THEN the TokenAuthenticationService should return an actual Authentication with the expected username")
    public void givenTokenAuthenticationServiceWithFakeValuesWhenGetAuthenticationCalledThenTokenAuthenticationServiceReturnActualAuthentication() {
        final var secret = Encoders.BASE64.encode("AFakeSecretThatNeedsToBeUsedForTestingPurposesIGuess?".getBytes(StandardCharsets.UTF_8));
        final var tokenAuthenticationService = new TokenAuthenticationService(System.currentTimeMillis(), secret);
        final var expectedUsername = "Fake Subject";
        final var actualToken = tokenAuthenticationService.buildJWTToken(expectedUsername);

        final var mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        when(mockHttpServletRequest.getHeader(eq(JWTConfigurationConstants.AUTHORIZATION_HEADER_STRING))).thenReturn(actualToken);

        final var actualAuthentication = tokenAuthenticationService.getAuthentication(mockHttpServletRequest);
        assertNotNull(actualAuthentication);
        assertTrue(actualAuthentication.isAuthenticated());
        assertEquals(expectedUsername, String.valueOf(actualAuthentication.getPrincipal()));
    }
}