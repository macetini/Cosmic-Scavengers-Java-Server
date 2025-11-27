package com.cosmic.scavengers.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.meta.PlayerEntity;
import com.cosmic.scavengers.db.meta.World;
import com.cosmic.scavengers.db.repos.PlayerEntityRepository;

/**
 * Service dedicated to retrieving a player's current game state, including
 * their current World.
 */
@Service
public class PlayerStateService {

	private final PlayerEntityRepository playerEntityRepository;
	private final WorldService worldService;

	// Assuming PlayerRepository exists to fetch the Player object by ID
	public PlayerStateService(PlayerEntityRepository playerEntityRepository, WorldService worldService) {
		this.playerEntityRepository = playerEntityRepository;
		this.worldService = worldService;
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
	public World getCurrentWorldByPlayerId(Long playerId) {
		final PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
				.orElseThrow(() -> new IllegalArgumentException("Player Entity not found with ID: " + playerId));

		return playerEntity.getWorld();
	}
}