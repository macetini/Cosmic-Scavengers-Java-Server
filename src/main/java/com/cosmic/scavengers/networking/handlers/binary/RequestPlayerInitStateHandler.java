package com.cosmic.scavengers.networking.handlers.binary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;

import io.netty.buffer.ByteBuf;

@Component
public class RequestPlayerInitStateHandler implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(RequestPlayerInitStateHandler.class);

	public RequestPlayerInitStateHandler() {				
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_PLAYER_INIT_STATE_C;
	}
	
	@Override
	public void handle(Long playerId, ByteBuf payload) {
		log.info("Getting Player Init State for Player ID: '{}'.", playerId);	
				
//		Worlds worlds = playerInitService.getCurrentWorldDataByPlayerId(playerId);
//		
//		WorldData worldData = WorldData.newBuilder()
//				.setId(worlds.getId())
//				.setWorldName(worlds.getName())
//				.setMapSeed(0) // mapSeed removed from entity
//				.setSectorSizeUnits(0) // sectorSizeUnits removed from entity
//				.setConfigJson(worlds.getConfig().data())
//				.build();
	}	
}
