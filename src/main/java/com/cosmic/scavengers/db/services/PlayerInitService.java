package com.cosmic.scavengers.db.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.jooq.repositories.PlayerEntitiyJooqRepository;
import com.cosmic.scavengers.db.jooq.repositories.PlayerJooqRepository;
import com.cosmic.scavengers.db.jpa.domain.World;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.gameplay.registries.WorldsRegistry;

@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final PlayerEntitiyJooqRepository playerEntityJooqRepository;
	private final PlayerJooqRepository playerJooqRepository;
	private final WorldsRegistry worldRegisty;
	
	public PlayerInitService(			
			PlayerEntitiyJooqRepository playerEntityJooqRepository,
			PlayerJooqRepository playerJooqRepository,
			WorldsRegistry worldRegisty
			) {		
		this.playerEntityJooqRepository = playerEntityJooqRepository;
		this.playerJooqRepository = playerJooqRepository;
		this.worldRegisty = worldRegisty;
	}

	public World getCurrentWorldDataByPlayerId(long playerId) {
        log.debug("Fetching world data for player {}", playerId);        

        Long worldId = playerJooqRepository.getCurrentWorldId(playerId)
        		.orElseThrow(() -> new IllegalStateException("Player ID " + playerId + " does not exist or has no assigned world."));
        
        log.debug("Player Id: '{}' is in World Id: '{}'", playerId, worldId);
        
        return worldRegisty.get(worldId)
				.orElseThrow(() -> new IllegalStateException("World ID " + worldId + " does not exist in the registry."));
    }

	/**
	 * Fetches entities from DB and ensures they exist in the live ECS simulation.
	 */
	public List<PlayerEntities> fetchAllPlayerEntities(long playerId) {
		log.debug("Fetching All Entities for Player Id: '{}'", playerId);
		
		return playerEntityJooqRepository.getAllByPlayerId(playerId);		
	}
}
