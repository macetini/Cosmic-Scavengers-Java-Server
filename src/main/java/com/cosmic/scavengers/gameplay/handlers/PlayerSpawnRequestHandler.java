package com.cosmic.scavengers.gameplay.handlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.services.PlayerInitService;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetAllPlayerEntitiesRequest;
import com.cosmic.scavengers.gameplay.services.SpawnService;

@Component
public class PlayerSpawnRequestHandler implements IGameplayRequestHandler<GetAllPlayerEntitiesRequest> {
	private static final Logger log = LoggerFactory.getLogger(PlayerSpawnRequestHandler.class);
	private final PlayerInitService playerInitService;
	private final SpawnService spawnService;

	public PlayerSpawnRequestHandler(
			PlayerInitService playerInitService, 
			SpawnService spawnService) {
		this.playerInitService = playerInitService;
		this.spawnService = spawnService;
	}

	@Override
	public boolean canHandle(IGameplayRequest request) {
		return request instanceof GetAllPlayerEntitiesRequest;
	}

	@Override
	public void handle(GetAllPlayerEntitiesRequest request) {
		log.debug("Processing spawn request for player {}", request.playerId());

		Long playerId = request.playerId();

		final List<PlayerEntities> playerEntities = 
				playerInitService.fetchAllPlayerEntities(request.playerId());

		if (playerEntities.isEmpty()) {
			log.error("No player entities found for playerId '{}'", playerId);
			return;
		}
		log.debug("Found {} entities for player ID {}.", playerEntities.size(), playerId);

		spawnService.spawnEntities(request.playerId(), playerEntities);		
	}
}
