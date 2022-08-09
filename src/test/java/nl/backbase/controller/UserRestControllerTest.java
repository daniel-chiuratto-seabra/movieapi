package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This {@link IntegrationTest} implementation, runs an Integration Test focusing in the "/v1/signup" and "/v1/signin?
 * endpoints, testing both operations
 *
 * @author Daniel Chiuratto Seabra
 * @since 06/08/2022
 */
class UserRestControllerTest extends IntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("GIVEN the UserRestController " +
				  "WHEN an User is requested to be created " +
				  "THEN it should be available in database as expected")
	public void givenUserRestControllerWhenAnUserIsRequestedToBeCreatedThenItShouldBeAvailableInDatabaseAsExpected() {
		// Here the operations for Signing Up and Singing In are available in the parent IntegrationTest class
		// that this class extends, below you have just the verification in the User Repository that the user has been
		// successfully created

		// Therefore, here we look for the username from the UserDTO available in the parent class
		final var actualUserEntity = this.userRepository.findByUsername(this.userDto.getUsername());
		// Confirm that the entity returned by the repository is not null
		assertNotNull(actualUserEntity);
		// And assert that both username and password matches as expected
		assertEquals(this.userDto.getUsername(), actualUserEntity.getUsername());
		assertTrue(this.passwordEncoder.matches(this.userDto.getPassword(), actualUserEntity.getPassword()));
	}
}
