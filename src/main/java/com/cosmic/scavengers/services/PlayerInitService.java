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
import com.cosmic.scavengers.db.repos.PlayerEntityRepository;

/**
 * Service dedicated to managing the initial game state creation for a player.
 * Uses the WorldService to abstract access to world data.
 */
@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final PlayerEntityRepository playerEntityRepository;
	private final WorldService worldService;

	// Updated constructor to use WorldService
	public PlayerInitService(PlayerEntityRepository playerEntityRepository, WorldService worldService) {
		this.playerEntityRepository = playerEntityRepository;
		this.worldService = worldService;
	}

	/**
	 * Public method called during login/registration to ensure the player's core
	 * game entities exist.
	 * 
	 * @param player The authenticated Player object.
	 * 
	 */
	@Transactional // Ensures the entity creation is an atomic database operation
	public void ensurePlayerInitialized(Player player) {
		createMainBaseIfMissing(player);
		// Add other initialization calls here (e.g., giveStartingUnitsIfMissing)
	}

	/**
	 * Checks if the player has any entities and creates a starter SHIP at (0, 0) if
	 * none exist. This is an idempotent check.
	 */
	private void createMainBaseIfMissing(Player player) {
		List<PlayerEntity> entities = playerEntityRepository.findAllByPlayerId(player.getId());

		if (entities.isEmpty()) {
			log.info("Creating initial starter MAIN_BASE for player (ID: {} - UName: {}).", player.getId(),
					player.getUsername());

			World defaultWorld = worldService.getDefaultWorld();

			PlayerEntity newPlayerEntity = new PlayerEntity(player, // Player object
					defaultWorld, // World object
					"MAIN_BASE", // entityType
					0, // chunkX (Integer)
					0, // chunkY (Integer)
					0.0f, // posX (Float, using 0.0f)
					0.0f, // posY (Float, using 0.0f)
					100, // health (Integer)
					"{\"resource\": 50}", // stateData (JSON String)
					Instant.now() // createdAt (Instant)
			);

			playerEntityRepository.save(newPlayerEntity);

			log.info("Successfully created initial STARTER_SHIP for player {}.", player.getUsername());
		}
	}

}