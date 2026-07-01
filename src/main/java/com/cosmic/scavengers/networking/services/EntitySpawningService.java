package com.cosmic.scavengers.networking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.gameplay.queue.GameplayRequestQueue;
import com.cosmic.scavengers.gameplay.queue.requests.InitSpawnRequest;

import dev.dominion.ecs.api.Dominion;
import io.netty.channel.ChannelId;

@Service
public class EntitySpawningService {
    private static final Logger log = LoggerFactory.getLogger(EntitySpawningService.class);
        
    private final GameplayRequestQueue gameplayRequestQueue;

	public EntitySpawningService(GameplayRequestQueue gameplayRequestQueue) {
		this.gameplayRequestQueue = gameplayRequestQueue;
	}
    
    public void processPlayerEntitiesSpawnRequest(ChannelId id, Long playerId) {
    	log.debug("Processing Player Entities Spawn Request for Player ID: '{}'", playerId);
    	
    	InitSpawnRequest request = new InitSpawnRequest(id, playerId);
    	gameplayRequestQueue.submit(request);
    }
}