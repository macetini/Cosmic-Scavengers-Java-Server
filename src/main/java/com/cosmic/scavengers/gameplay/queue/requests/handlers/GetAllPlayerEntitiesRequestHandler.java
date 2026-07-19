package com.cosmic.scavengers.gameplay.queue.requests.handlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.executor.DatabaseExecutor;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.services.PlayerInitService;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetAllPlayerEntitiesRequest;
import com.cosmic.scavengers.gameplay.services.SpawnService;

@Component
public class GetAllPlayerEntitiesRequestHandler implements IGameplayRequestHandler<GetAllPlayerEntitiesRequest> {
	private static final Logger log = LoggerFactory.getLogger(GetAllPlayerEntitiesRequestHandler.class);
	
	private final PlayerInitService playerInitService;
	private final SpawnService spawnService;
	private final DatabaseExecutor dbExecutor;

	public GetAllPlayerEntitiesRequestHandler(
			PlayerInitService playerInitService, 
			SpawnService spawnService, 
			DatabaseExecutor dbExecutor) {
		this.playerInitService = playerInitService;
		this.spawnService = spawnService;
		this.dbExecutor = dbExecutor;
	}

	@Override
	public Class<GetAllPlayerEntitiesRequest> getSupportedRequestType() {
		return GetAllPlayerEntitiesRequest.class;
	}

	@Override
	public boolean canHandle(IGameplayRequest request) {
		return request instanceof GetAllPlayerEntitiesRequest;
	}
	
	@Override
    public void handle(GetAllPlayerEntitiesRequest request) {
        log.debug("Processing spawn request for player {}", request.playerId());
        Long playerId = request.playerId();

        dbExecutor.execute(() -> {
            final List<PlayerEntities> playerEntities = 
                    playerInitService.fetchAllPlayerEntities(playerId);

            if (playerEntities.isEmpty()) {
                log.error("No player entities found for playerId '{}'", playerId);
                return;
            }
            log.debug("Found {} entities for player ID {}.", playerEntities.size(), playerId);
           
            spawnService.spawnEntities(playerId, playerEntities); 
        });
    }
}
