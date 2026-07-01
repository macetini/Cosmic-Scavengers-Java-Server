package com.cosmic.scavengers.networking.handlers.binary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.constants.NetworkAttributeKeys;
import com.cosmic.scavengers.networking.services.EntitySpawningService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

@Component
public class RequestPlayerEntities implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(RequestPlayerEntities.class);

	private final EntitySpawningService entitySpawningService;

	public RequestPlayerEntities(EntitySpawningService entitySpawningService) {
		this.entitySpawningService = entitySpawningService;
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_PLAYER_ENTITIES_C;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, ByteBuf payload) {
		log.info("Handling Command: [{}] | Channel: '{}'.", getCommand().getLogText(), ctx.channel().id());

		ChannelId channelId = ctx.channel().id();

		Long playerId = (Long) ctx.channel().attr(NetworkAttributeKeys.PLAYER_ID.getKey()).get();
		if (playerId == null) {
			throw new IllegalStateException("Player ID not found in channel attributes for channel: " + ctx.channel().id());
		}

		Long payloadPlayerId = payload.readLong();

		if (!playerId.equals(payloadPlayerId)) {
			log.error("Player ID mismatch: Channel PlayerId '{}' does not match Payload PlayerId '{}'.", playerId, payloadPlayerId);
			return;
		}

		entitySpawningService.processPlayerEntitiesSpawnRequest(channelId, playerId);
	}

	public void handle(ChannelHandlerContext ctx, Long playerId) {
		log.info("Handling Command: [{}] | Channel: '{}'.", getCommand().getLogText(), ctx.channel().id());

		entitySpawningService.processPlayerEntitiesSpawnRequest(ctx.channel().id(), playerId);
	}
}
