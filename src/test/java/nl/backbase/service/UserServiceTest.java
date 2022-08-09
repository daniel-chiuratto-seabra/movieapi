package nl.backbase.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.backbase.controller.exception.UserAlreadyExistException;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.UserMappers;
import nl.backbase.model.UserEntity;
import nl.backbase.repository.UserRepository;

class UserServiceTest {

    private UserService userService;

    private UserRepository mockUserRepository;
    private UserMappers mockUserMappers;

    @BeforeEach
    private void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.mockUserMappers = mock(UserMappers.class);
        this.userService = new UserService(this.mockUserRepository, this.mockUserMappers);
    }

    @Test
    @DisplayName("GIVEN a UserService with mocked dependencies " +
                  "WHEN the saveUserDTO is called with an expected fake UserDTO being set " +
                   "AND the UserRepository returns an UserEntity " +
                  "THEN an UserAlreadyExistException should be thrown")
    public void givenUserServiceWhenSaveUserDTOIsCalledThenAnUserAlreadyExistExceptionShouldBeThrown() {
        final var expectedFakeUsername = "Fake Username";

        final var expectedFakeUserDTO = new UserDTO();
        expectedFakeUserDTO.setUsername(expectedFakeUsername);

        final var fakeExpectedUserEntity = new UserEntity();
        when(this.mockUserRepository.findByUsername(eq(expectedFakeUsername))).thenReturn(fakeExpectedUserEntity);

        assertThrows(UserAlreadyExistException.class, () -> this.userService.saveUserDTO(expectedFakeUserDTO));
    }

    @Test
    @DisplayName("GIVEN a UserService with mocked dependencies " +
                  "WHEN the saveUserDTO is called with an expected fake UserDTO being set " +
                   "AND the UserRepository returns null " +
                  "THEN the fake UserDTO should be mapped into an expected fake UserEntity " +
                   "AND it is saved by the UserRepository through save method")
    public void givenUserServiceWhenTheSaveUserDTOIsCalledThenTheFakeUserDTOShouldMappedAndSavedByUserRepository() {
        final var expectedFakeUsername = "Fake Username";
        final var expectedFakeUserDTO = new UserDTO();
        expectedFakeUserDTO.setUsername(expectedFakeUsername);

        final var expectedFakeUserEntity = new UserEntity();
        when(this.mockUserMappers.userDTOToUserEntity(eq(expectedFakeUserDTO))).thenReturn(expectedFakeUserEntity);

        this.userService.saveUserDTO(expectedFakeUserDTO);

        verify(this.mockUserRepository).findByUsername(eq(expectedFakeUsername));
        verify(this.mockUserMappers).userDTOToUserEntity(eq(expectedFakeUserDTO));
        verify(this.mockUserRepository).save(eq(expectedFakeUserEntity));
    }

}