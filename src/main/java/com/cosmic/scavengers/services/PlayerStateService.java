package com.cosmic.scavengers.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.meta.PlayerEntity;
import com.cosmic.scavengers.db.meta.World;
import com.cosmic.scavengers.db.repos.PlayerEntityRepository;
import com.cosmic.scavengers.networking.meta.WorldData;

/**
 * Service dedicated to retrieving a player's current game state, including
 * their current World.
 */
@Service
public class PlayerStateService {
	Logger log = org.slf4j.LoggerFactory.getLogger(PlayerStateService.class);

	private final PlayerEntityRepository playerEntityRepository;
	private final WorldService worldService;

	// Assuming PlayerRepository exists to fetch the Player object by ID
	public PlayerStateService(PlayerEntityRepository playerEntityRepository, WorldService worldService) {
		this.playerEntityRepository = playerEntityRepository;
		this.worldService = worldService;
	}

	/**
	 * Finds the current World's metadata (as a DTO) for a given player. The
	 * transactional scope ensures all necessary fields are initialized.
	 *
	 * @param playerId The ID of the player.
	 * @return The network-ready WorldData DTO.
	 */
	@Transactional(readOnly = true)
	public Optional<WorldData> getCurrentWorldDataByPlayerId(Long playerId) {
		final PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
				.orElseThrow(() -> new IllegalArgumentException("Player Entity not found with ID: " + playerId));

		World currentWorld = playerEntity.getWorld();
		
		log.info("Retrieved world ID {} for player ID {}.", currentWorld.getId(), playerId);

		return worldService.toWorldData(currentWorld);
	}

	/**
	 * Finds the current World a Player is active in, given the Player's ID.
	 *
	 * @param playerId The ID of the player.
	 * 
	 * @return The World entity the player is currently in.
	 * 
	 * @throws IllegalArgumentException if the PlayerEntity is not found.
	 */
	@Transactional(readOnly = true)
	protected World getCurrentWorldByPlayerId(Long playerId) {
		final PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
				.orElseThrow(() -> new IllegalArgumentException("Player Entity not found with ID: " + playerId));

		return playerEntity.getWorld();
	}
}