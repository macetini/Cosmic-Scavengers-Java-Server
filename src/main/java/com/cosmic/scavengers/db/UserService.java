package com.cosmic.scavengers.db;

import com.cosmic.scavengers.core.SecurityUtils;
import org.springframework.stereotype.Service;

/**
 * Service layer for player account management (Login and Registration). This
 * class orchestrates security (hashing) and data access (JPA Repository).
 */
@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		// Spring automatically injects the JpaRepository implementation
		this.userRepository = userRepository;
	}

	/**
	 * Registers a new user account. * @param username The desired username.
	 * 
	 * @param plaintextPassword The user's password in plain text.
	 * @return The newly created Player entity, or null if the username already
	 *         exists.
	 */
	public Player registerUser(String username, String plaintextPassword) {
		// Check if username is already taken
		if (userRepository.findByUsername(username) != null) {
			return null;
		}

		// 1. Generate Salt and Hash using SecurityUtils
		String salt = SecurityUtils.generateSalt();
		String hash = SecurityUtils.hashPassword(plaintextPassword, salt);

		// 2. Create and Save Player
		Player newPlayer = new Player(username, hash, salt);
		return userRepository.save(newPlayer);
	}

	/**
	 * Authenticates a user by checking the provided password against the stored
	 * hash. * @param username The username provided during login.
	 * 
	 * @param plaintextPassword The password provided during login.
	 * @return The authenticated Player entity, or null on failure (user not found
	 *         or bad password).
	 */
	public Player loginUser(String username, String plaintextPassword) {
		// 1. Find user by username
		Player player = userRepository.findByUsername(username);
		if (player == null) {
			return null; // User not found
		}

		// 2. Verify Password using the stored hash and salt
		boolean authenticated = SecurityUtils.verifyPassword(plaintextPassword, player.getPasswordHash(),
				player.getSalt());

		return authenticated ? player : null;
	}
}