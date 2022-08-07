package nl.backbase.controller;

import nl.backbase.IntegrationTest;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

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
		final var actualUserEntity = this.userRepository.findByUsername(this.userDto.getUsername());
		assertNotNull(actualUserEntity);
		assertEquals(this.userDto.getUsername(), actualUserEntity.getUsername());
		assertTrue(this.passwordEncoder.matches(this.userDto.getPassword(), actualUserEntity.getPassword()));
	}
}
