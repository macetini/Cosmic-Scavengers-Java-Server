package com.cosmic.scavengers.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.meta.PlayerEntity;
import com.cosmic.scavengers.db.meta.World;
import com.cosmic.scavengers.db.repos.PlayerEntityRepository;
import com.cosmic.scavengers.networking.meta.PlayerEntityDTO;
import com.cosmic.scavengers.networking.meta.WorldDataDTO;

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
	public Optional<WorldDataDTO> getCurrentWorldDataByPlayerId(Long playerId) {
		final PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
				.orElseThrow(() -> new IllegalArgumentException("Player Entity not found with ID: " + playerId));
		World currentWorld = playerEntity.getWorld();

		log.info("Retrieved world ID {} for player ID {}.", currentWorld.getId(), playerId);
		return worldService.toWorldData(currentWorld);
	}

	/**
	 * Retrieves all PlayerEntity objects owned by a specific player ID. * @param
	 * playerId The ID of the Player.
	 * 
	 * @return A list of PlayerEntity objects.
	 */
	@Transactional(readOnly = true)
	public List<PlayerEntityDTO> getEntitiesByPlayerId(Long playerId) {
		List<PlayerEntity> playerEntites = playerEntityRepository.findAllByPlayerId(playerId);

		if (playerEntites.isEmpty()) {
			throw new IllegalArgumentException("No Player Entities found for Player ID: '" + playerId
					+ "'.  This should not be possible, each player has to have at least 1 enetity.");
		}

		return playerEntites.stream().map(PlayerEntityDTO::fromEntity).toList();
	}
}