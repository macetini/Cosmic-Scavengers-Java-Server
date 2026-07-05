package com.cosmic.scavengers.networking.handlers.binary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.services.EntitySpawningService;

import io.netty.buffer.ByteBuf;

@Component
public class RequestPlayerEntitiesHandler implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(RequestPlayerEntitiesHandler.class);

	private final EntitySpawningService entitySpawningService;

	public RequestPlayerEntitiesHandler(EntitySpawningService entitySpawningService) {
		this.entitySpawningService = entitySpawningService;
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_PLAYER_ENTITIES_C;
	}

	@Override
	public void handle(Long playerId, ByteBuf payload) {		
		log.info("Handling Command: [{}] | Player ID: '{}'.", getCommand().getLogText(), playerId);		
		entitySpawningService.processPlayerEntitiesSpawnRequest(playerId);
	}
}
