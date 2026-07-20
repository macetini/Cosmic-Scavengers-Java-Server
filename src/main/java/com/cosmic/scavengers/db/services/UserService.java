package com.cosmic.scavengers.db.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.utils.SecurityUtil;
import com.cosmic.scavengers.db.jooq.repositories.PlayerJooqRepository;
import com.cosmic.scavengers.db.jpa.domain.Player;
import com.cosmic.scavengers.db.jpa.repositories.PlayerJpaRepository;
import com.cosmic.scavengers.db.model.tables.pojos.Players;

/**
 * Service layer for player account management (Login and Registration). This
 * class orchestrates security (hashing) and data access (JPA Repository).
 */
@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final PlayerJooqRepository jooqPlayerRepository;
	private final PlayerJpaRepository jpaPlayerRepository; // Add JPA Repo

	public UserService(PlayerJooqRepository jooqPlayerRepository, PlayerJpaRepository jpaPlayerRepository) {
		this.jooqPlayerRepository = jooqPlayerRepository;
		this.jpaPlayerRepository = jpaPlayerRepository;
	}

	@Transactional
	public Optional<Player> registerUser(String username, String plaintextPassword) {
		final String salt = SecurityUtil.generateSalt();
		final String hash = SecurityUtil.hashPassword(plaintextPassword, salt);
		return registerNewPlayer(username, hash, salt);
	}

	protected Optional<Player> registerNewPlayer(String username, String hashedPassword, String salt) {
		if (jpaPlayerRepository.existsByUsername(username)) {
			log.warn("Attempted to register a new player with an existing username: {}", username);
			return Optional.empty();
		}

		Player newPlayer = new Player();
		newPlayer.setUsername(username);
		newPlayer.setPasswordHash(hashedPassword);
		newPlayer.setSalt(salt);
		newPlayer.setCurrentWorldId(1L); // Default starting world ID

		// createdAt is automatically handled by @PrePersist in the Entity!
		Player insertedPlayer = jpaPlayerRepository.save(newPlayer);
		return Optional.of(insertedPlayer);
	}

	@Transactional
	public Optional<Players> loginUser(String username, String plaintextPassword) {
		final Optional<Players> playerOptional = jooqPlayerRepository.findByUsername(username);
		if (playerOptional.isEmpty()) {
			log.info("Login failed: User '{}' not found.", username);
			return Optional.empty(); // User not found
		}
		Players player = playerOptional.get();

		final boolean authenticated = 
				SecurityUtil.verifyPassword(
						plaintextPassword, 
						player.getPasswordHash(), 
						player.getSalt());

		if (!authenticated) {
			log.info("Authentication failed for user '{}': Incorrect password.", username);
			return Optional.empty(); // Authentication failed
		}

		log.info("User '{}' logged and authenticated successfully.", username);
		return Optional.of(player);
	}
}
