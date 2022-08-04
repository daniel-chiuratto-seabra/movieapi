package nl.backbase.mapper;

import nl.backbase.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class UserMappersTest {

    private final UserMappers userMappers = new UserMappers(new BCryptPasswordEncoder());

    @Test
    @DisplayName("GIVEN a null UserDTO, WHEN the mapper tries to map it, THEN it should return a null value")
    public void givenANullUserDTOWhenTheMapperTriesToMapItThenItShouldReturnANullValue() {
        final var userEntity = this.userMappers.userDTOToUserEntity(null);
        assertNull(userEntity);
    }

    @Test
    @DisplayName("GIVEN a fake UserDTO, WHEN the mapper tries to map it, THEN it should return an actual UserEntity with expected values")
    public void givenAFakeUserDTOWhenTheMapperTriesToMapItThenItShouldReturnAnActualUserEntityWithExpectedValues() {
        final var passwordEncoder = new BCryptPasswordEncoder();

        final var expectedFakePassword = "Fake Password";
        final var expectedFakeUserDTO = new UserDTO();

        expectedFakeUserDTO.setUsername("Fake User Name");
        expectedFakeUserDTO.setPassword(expectedFakePassword);

        final var actualUserEntity = this.userMappers.userDTOToUserEntity(expectedFakeUserDTO);
        assertNotNull(actualUserEntity);
        assertEquals(expectedFakeUserDTO.getUsername(), actualUserEntity.getUsername());
        assertTrue(passwordEncoder.matches(expectedFakePassword, actualUserEntity.getPassword()));
    }
}
