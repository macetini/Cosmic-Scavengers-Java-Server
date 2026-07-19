package com.cosmic.scavengers.gameplay.queue.requests.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.executor.DatabaseExecutor;
import com.cosmic.scavengers.db.services.PlayerInitService;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetWorldDataRequest;
import com.cosmic.scavengers.gameplay.services.SpawnService;

@Component
public class GetWorldDataRequestHandler implements IGameplayRequestHandler<GetWorldDataRequest> {
	private static final Logger log = LoggerFactory.getLogger(GetWorldDataRequestHandler.class);
	
	private final PlayerInitService playerInitService;	
	private final DatabaseExecutor dbExecutor;
	
	public GetWorldDataRequestHandler(
			PlayerInitService playerInitService, 
			DatabaseExecutor dbExecutor) {
		this.playerInitService = playerInitService;		
		this.dbExecutor = dbExecutor;
	}
	
	@Override
	public Class<GetWorldDataRequest> getSupportedRequestType() {
		return GetWorldDataRequest.class;
	}

	@Override
	public boolean canHandle(IGameplayRequest request) {
		return request instanceof GetWorldDataRequest;
	}

	@Override
    public void handle(GetWorldDataRequest request) {
		log.info("Handling GetWorldDataRequest for player ID: {}", request.playerId());
		
		dbExecutor.execute(() -> {
			try {
				final var worldData = playerInitService.getCurrentWorldDataByPlayerId(request.playerId());
				if (worldData == null) {
					log.error("No world data found for playerId '{}'", request.playerId());
					return;
				}
				log.info("Successfully fetched world data for player ID: {}", request.playerId());
				// Here you would typically send the worldData back to the player or process it further.
			} catch (Exception e) {
				log.error("Error fetching world data for player ID {}: {}", request.playerId(), e.getMessage(), e);
			}
		});
	}
}
