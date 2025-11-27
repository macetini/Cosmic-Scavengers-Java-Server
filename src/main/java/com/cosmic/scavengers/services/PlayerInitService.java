package com.cosmic.scavengers.services;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.meta.Player;
import com.cosmic.scavengers.db.meta.PlayerEntity;
import com.cosmic.scavengers.db.meta.World;
import com.cosmic.scavengers.db.repo.PlayerEntityRepository;
import com.cosmic.scavengers.db.repo.WorldRepository; // Import WorldRepository

/**
 * Service dedicated to managing the initial game state creation for a player.
 * This includes idempotent checks to ensure a player always has required
 * starting entities.
 */
@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final PlayerEntityRepository playerEntityRepository;
	private final WorldRepository worldRepository; // Dependency added

	// Updated constructor to include WorldRepository
	public PlayerInitService(PlayerEntityRepository playerEntityRepository, WorldRepository worldRepository) {
		this.playerEntityRepository = playerEntityRepository;
		this.worldRepository = worldRepository;
	}

	/**
	 * Public method called during login/registration to ensure the player's core
	 * game entities exist. This is the new, centralized entry point for initial
	 * game state setup. * @param player The authenticated Player object.
	 * 
	 * @return void
	 */
	@Transactional // Ensures the entity creation is an atomic database operation
	public void ensurePlayerInitialized(Player player) {
		// We only check for the MAIN_BASE for simplicity, but more complex logic
		// could check for all required starting resources/units.
		createMainBaseIfMissing(player);

		// Add other initialization calls here (e.g., giveStartingUnitsIfMissing)
	}

	/**
	 * Checks if the player has any entities and creates a "MAIN_BASE" at (0, 0) if
	 * none exist. This is an idempotent check.
	 */
	private void createMainBaseIfMissing(Player player) {
		List<PlayerEntity> entities = playerEntityRepository.findAllByPlayerId(player.getId());

		if (entities.isEmpty()) {
			log.info("Creating initial MAIN_BASE for player (ID: {} - UName: {}).", player.getId(),
					player.getUsername());

			// 1. Fetch the required World object.
			// Assuming the first world (ID 1L) is the starting world.
			World defaultWorld = worldRepository.findById(1L).orElseThrow(
					() -> new IllegalStateException("Default World ID 1 not found. Cannot create player base."));

			// 2. Instantiate PlayerEntity using the correct constructor signature
			PlayerEntity mainBase = new PlayerEntity(player, // Player object
					defaultWorld, // World object (Now correctly defined)
					"MAIN_BASE", // entityType
					0, // chunkX (Integer)
					0, // chunkY (Integer)
					0.0f, // posX (Float, using 0.0f)
					0.0f, // posY (Float, using 0.0f)
					500, // health (Integer)
					"{\"resource\": 50}", // stateData (JSON String)
					Instant.now() // createdAt (Instant)
			);

			playerEntityRepository.save(mainBase);
			log.info("Successfully created initial MAIN_BASE for player {}.", player.getUsername());
		}
	}
}