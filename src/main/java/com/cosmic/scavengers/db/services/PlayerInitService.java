package com.cosmic.scavengers.db.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.jooq.repositories.PlayerEntitiyJooqRepository;
import com.cosmic.scavengers.db.jooq.repositories.PlayerJooqRepository;
import com.cosmic.scavengers.db.jooq.repositories.WorldJooqRepository;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;

@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final PlayerEntitiyJooqRepository playerEntityJooqRepository;
	private final PlayerJooqRepository playerJooqRepository;
	private final WorldJooqRepository worldJooqRepository;
	
	public PlayerInitService(			
			PlayerEntitiyJooqRepository playerEntityJooqRepository,
			PlayerJooqRepository playerJooqRepository,
			WorldJooqRepository worldJooqRepository
			) {		
		this.playerEntityJooqRepository = playerEntityJooqRepository;
		this.playerJooqRepository = playerJooqRepository;
		this.worldJooqRepository = worldJooqRepository;
	}

	public Worlds getCurrentWorldDataByPlayerId(long playerId) {
        log.info("Fetching world data for player {}", playerId);        

        Long worldId = playerJooqRepository.getCurrentWorldId(playerId)
        		.orElseThrow(() -> new IllegalStateException("Player ID " + playerId + " does not exist or has no assigned world."));

        return worldJooqRepository.getById(worldId)
                .orElseThrow(() -> new IllegalStateException("World ID " + worldId + " does not exist."));
    }

	/**
	 * Fetches entities from DB and ensures they exist in the live ECS simulation.
	 */
	public List<PlayerEntities> fetchAllPlayerEntities(long playerId) {
		log.info("Fetching All Entities for Player Id: '{}'", playerId);
		
		return playerEntityJooqRepository.getAllByPlayerId(playerId);		
	}
}
