package nl.backbase.security.config;

import nl.backbase.controller.exception.InvalidPasswordException;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class JWTMovieAPIAuthenticationManagerTest {

    private JWTMovieAPIAuthenticationManager jwtMovieAPIAuthenticationManager;

    private UserRepository mockUserRepository;
    private PasswordEncoder mockPasswordEncoder;

    @BeforeEach
    public void setUp() {
        this.mockUserRepository = Mockito.mock(UserRepository.class);
        this.mockPasswordEncoder = Mockito.mock(PasswordEncoder.class);
        this.jwtMovieAPIAuthenticationManager = new JWTMovieAPIAuthenticationManager(this.mockUserRepository, this.mockPasswordEncoder);
    }

    @Test
    @DisplayName("GIVEN a JWTMovieAPIAuthenticationManager with mocked dependencies " +
                  "WHEN the authenticate method is called with an expected fake UsernamePasswordAuthenticationToken set " +
                   "AND the UserRepository returns null for the informed username " +
                  "THEN an UsernameNotFoundException should be thrown")
    public void givenJWTMovieAPIAuthenticationManagerWhenAuthenticateIsCalledThenUsernameNotFoundExceptionShouldBeThrown() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakePassword = "Fake Password";
        assertThrows(UsernameNotFoundException.class, () -> this.jwtMovieAPIAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(expectedFakeUsername, expectedFakePassword)));
    }

    @Test
    @DisplayName("GIVEN a JWTMovieAPIAuthenticationManager with mocked dependencies " +
                  "WHEN the authenticate method is called with an expected fake UsernamePasswordAuthenticationToken set " +
                   "AND the UserRepository returns a fake UserEntity " +
                   "AND the PasswordEncoder returns that the UserEntity and UsernamePasswordAuthenticationToken passwords does not match " +
                  "THEN an InvalidPasswordException should be thrown")
    public void givenJWTMovieAPIAuthenticationManagerWhenAuthenticateWithUsernamePasswordAuthenticationTokenAndUserRepositoryReturnsUserEntityAndPasswordEncoderReturnsFalseThenInvalidPasswordExceptionShouldBeThrown() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakePassword = "Fake Password";
        final var expectedAuthentication = new UsernamePasswordAuthenticationToken(expectedFakeUsername, expectedFakePassword);

        final var expectedFakeUserEntity = new UserEntity();
        expectedFakeUserEntity.setUsername(expectedFakeUsername);
        expectedFakeUserEntity.setPassword("Another Fake Password");

        when(this.mockUserRepository.findByUsername(eq(expectedFakeUsername))).thenReturn(expectedFakeUserEntity);
        when(this.mockPasswordEncoder.matches(eq(expectedFakeUserEntity.getPassword()), eq(String.valueOf(expectedAuthentication.getCredentials())))).thenReturn(false);
        assertThrows(InvalidPasswordException.class, () -> this.jwtMovieAPIAuthenticationManager.authenticate(expectedAuthentication));
    }

    @Test
    @DisplayName("GIVEN a JWTMovieAPIAuthenticationManager with mocked dependencies " +
                  "WHEN the authenticate method is called with an expected fake UsernamePasswordAuthenticationToken set " +
                   "AND the UserRepository returns a fake UserEntity " +
                   "AND the PasswordEncoder returns that the UserEntity and UsernamePasswordAuthenticationToken passwords does match " +
                  "THEN an actual Authentication instance should be returned with the expected credentials")
    public void givenJWTMovieAPIAuthenticationManagerWhenAuthenticateCalledThenAnActualAuthenticationShouldBeReturnedWithExpectedCredentials() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakePassword = "Fake Password";
        final var expectedAuthentication = new UsernamePasswordAuthenticationToken(expectedFakeUsername, expectedFakePassword);

        final var expectedFakeUserEntity = new UserEntity();
        expectedFakeUserEntity.setUsername(expectedFakeUsername);
        expectedFakeUserEntity.setPassword(expectedFakePassword);

        when(this.mockUserRepository.findByUsername(eq(expectedFakeUsername))).thenReturn(expectedFakeUserEntity);
        when(this.mockPasswordEncoder.matches(eq(expectedFakeUserEntity.getPassword()), eq(String.valueOf(expectedAuthentication.getCredentials())))).thenReturn(true);

        final var actualAuthentication = this.jwtMovieAPIAuthenticationManager.authenticate(expectedAuthentication);
        assertNotNull(actualAuthentication);
        assertEquals(expectedFakeUserEntity.getUsername(), String.valueOf(actualAuthentication.getPrincipal()));
        assertEquals(expectedFakeUserEntity.getPassword(), String.valueOf(actualAuthentication.getCredentials()));
    }
}