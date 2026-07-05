package com.cosmic.scavengers.db.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.jooq.repositories.PlayerEntitiyJooqRepository;
import com.cosmic.scavengers.db.jooq.repositories.WorldJooqRepository;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;

@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final WorldJooqRepository worldJooqRepository;
	private final PlayerEntitiyJooqRepository playerEntityJooqRepository;	
	
	public PlayerInitService(WorldJooqRepository worldJooqRepository,
			PlayerEntitiyJooqRepository playerEntityJooqRepository) {
		this.worldJooqRepository = worldJooqRepository;
		this.playerEntityJooqRepository = playerEntityJooqRepository;		
	}

	public Worlds getCurrentWorldDataByPlayerId(long playerId) {
		log.info("Fetching world data for player {}", playerId);		

		final Optional<Worlds> worldOptional = worldJooqRepository.getById(playerId);
		return worldOptional
				.orElseThrow(() -> new IllegalStateException("No world data found for player with ID: " + playerId));
	}

	/**
	 * Fetches entities from DB and ensures they exist in the live ECS simulation.
	 */
	public List<PlayerEntities> fetchAllPlayerEntities(long playerId) {
		log.info("Fetching All Entities for Player Id: '{}'", playerId);
		
		return playerEntityJooqRepository.getAllByPlayerId(playerId);		
	}
}
