package com.cosmic.scavengers.services;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.SecurityUtils;
import com.cosmic.scavengers.db.meta.Player;
import com.cosmic.scavengers.db.repos.PlayerRepository;

/**
 * Service layer for player account management (Login and Registration). This
 * class orchestrates security (hashing) and data access (JPA Repository).
 */
@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final PlayerRepository userRepository;
	private final PlayerInitService playerInitService;

	public UserService(PlayerRepository userRepository, PlayerInitService playerInitService) {
		this.userRepository = userRepository;
		this.playerInitService = playerInitService;
	}

	/**
	 * Registers a new user account. * @param username The desired username.
	 * 
	 * @param plaintextPassword The user's password in plain text.
	 * @return The newly created Player entity, or null if the username already
	 *         exists.
	 */
	@Transactional
	public Optional<Player> registerUser(String username, String plaintextPassword) {
		// Check if username is already taken
		if (userRepository.findByUsername(username).isPresent()) {
			log.warn("Registration failed: Username '{}' is already taken.", username);
			return Optional.empty(); // Username already exists
		}

		// Generate Salt and Hash using SecurityUtils
		final String salt = SecurityUtils.generateSalt();
		final String hash = SecurityUtils.hashPassword(plaintextPassword, salt);

		// Create and Save Player
		final Player newPlayer = new Player(username, hash, salt, (new Date()).toInstant());
		final Player savedPlayer = userRepository.save(newPlayer);

		// Initialize Player's starting game state
		playerInitService.ensurePlayerInitialized(savedPlayer);

		return Optional.of(savedPlayer);
	}

	/**
	 * Authenticates a user by checking the provided password against the stored
	 * hash. * @param username The username provided during login.
	 * 
	 * @param plaintextPassword The password provided during login.
	 * @return The authenticated Player entity, or null on failure (user not found
	 *         or bad password).
	 */
	@Transactional
	public Optional<Player> loginUser(String username, String plaintextPassword) {
		final Optional<Player> playerOptional = userRepository.findByUsername(username);
		if (playerOptional.isEmpty()) {
			log.info("Login failed: User '{}' not found.", username);
			return Optional.empty(); // User not found
		}
		Player player = playerOptional.get();

		final boolean authenticated = SecurityUtils.verifyPassword(plaintextPassword, player.getPasswordHash(),
				player.getSalt());

		if (!authenticated) {
			log.info("Authentication failed for user '{}': Incorrect password.", username);
			return Optional.empty(); // Authentication failed
		}

		playerInitService.ensurePlayerInitialized(player);

		log.info("User '{}' logged and authenticated successfully.", username);
		return Optional.of(player);
	}
}