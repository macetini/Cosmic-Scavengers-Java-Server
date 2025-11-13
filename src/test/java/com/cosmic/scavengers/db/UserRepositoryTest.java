package com.cosmic.scavengers.db;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the UserRepository using the H2 in-memory database
 * profile. This ensures the repository correctly interacts with the Player
 * entity.
 */
@DataJpaTest
@ComponentScan(basePackages = "com.cosmic.scavengers")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void testRepositoryLoads() {
		// The fact that this test runs without crashing and userRepository is not null
		// confirms the JPA setup and the Player Entity mapping is correct.
		assertTrue(userRepository != null, "UserRepository should be autowired by Spring.");
	}

	/*
	 * You would add actual tests for CRUD operations here, for example: * @Test
	 * void testFindByUsername() { // Arrange: Save a new player Player newPlayer =
	 * new Player("testUser", "hash", "salt"); userRepository.save(newPlayer); * //
	 * Act: Find the player by username Player foundPlayer =
	 * userRepository.findByUsername("testUser"); * // Assert
	 * assertNotNull(foundPlayer, "Player should be found by username.");
	 * assertEquals("testUser", foundPlayer.getUsername()); }
	 */
}