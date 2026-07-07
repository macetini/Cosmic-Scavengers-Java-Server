package com.cosmic.scavengers.networking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.gameplay.queue.GameplayRequestQueue;
import com.cosmic.scavengers.gameplay.queue.requests.GetWorldDataRequest;

@Service
public class PlayerWorldService {
	private static final Logger log = LoggerFactory.getLogger(PlayerWorldService.class);
	
	private final GameplayRequestQueue gameplayRequestQueue;

	public PlayerWorldService(GameplayRequestQueue gameplayRequestQueue) {
		this.gameplayRequestQueue = gameplayRequestQueue;
	}
	
	public void processPlayerWorldDataRequest(Long playerId) {
		log.debug("Processing Player World Data Request for Player ID: '{}'", playerId);
		
		// Create a request to get the player's world data
		GetWorldDataRequest request = new GetWorldDataRequest(playerId);
		gameplayRequestQueue.submit(request);
	}

}
