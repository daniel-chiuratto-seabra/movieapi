package nl.backbase.security.service;

import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieAPIUserDetailsServiceTest {

    private MovieAPIUserDetailsService movieAPIUserDetailsService;
    private UserRepository mockUserRepository;

    @BeforeEach
    private void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.movieAPIUserDetailsService = new MovieAPIUserDetailsService(this.mockUserRepository);
    }

    @Test
    @DisplayName("GIVEN a MovieAPIUserDetailsService with mocked dependencies " +
                  "WHEN the loadUserByUsername is called with a fake username " +
                   "AND this username returns null from the database" +
                  "THEN UsernameNotFoundException should be thrown")
    public void givenMovieAPIUserDetailsServiceWhenLoadUserByUsernameIsCalledWithDatabaseReturnsNullThenUserNameNotFoundExceptionShouldBeThrown() {
        assertThrows(UsernameNotFoundException.class, () -> this.movieAPIUserDetailsService.loadUserByUsername("Fake Username"));
    }

    @Test
    @DisplayName("GIVEN a MovieAPIUserDetailsService with mocked dependencies " +
                  "WHEN the loadUserByUsername is called with a fake username " +
                   "AND this username returns an expected fake UserEntity from the database" +
                  "THEN an actual UserDetails should be returned with the expected credentials")
    public void givenMovieAPIUserDetailsServiceWhenLoadUserByUsernameCalledWithFakeUsernameAndUsernameReturnsExpectedUserEntityThenActualUserDetailsReturnedWithExpectedCredentials() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakePassword = "Fake Password";
        final var expectedFakeUserEntity = new UserEntity();
        expectedFakeUserEntity.setPassword(expectedFakePassword);

        when(this.mockUserRepository.findByUsername(eq(expectedFakeUsername))).thenReturn(expectedFakeUserEntity);

        final var actualUserDetails = this.movieAPIUserDetailsService.loadUserByUsername(expectedFakeUsername);

        assertNotNull(actualUserDetails);
        assertEquals(expectedFakeUsername, actualUserDetails.getUsername());
        assertEquals(expectedFakePassword, actualUserDetails.getPassword());
    }
}
