package com.cosmic.scavengers.gameplay.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetWorldDataRequest;

public class WorldDataRequestHandler implements IGameplayRequestHandler<GetWorldDataRequest> {
	private static final Logger log = LoggerFactory.getLogger(WorldDataRequestHandler.class);
	
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
		// Implement the logic to fetch and return world data for the player
	}
}
