package com.cosmic.scavengers.networking.handlers.binary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.proto.traits.MoveIntentProto;
import com.cosmic.scavengers.networking.services.EntityActionService;

import io.netty.buffer.ByteBuf;

@Component
public class EntityMoveHandler implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(EntityMoveHandler.class);

	private final EntityActionService entityActionService;

	public EntityMoveHandler(EntityActionService entityActionService) {
		this.entityActionService = entityActionService;
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_ENTITY_MOVE_C;
	}

	@Override
	public void handle(Long playerId, ByteBuf payload) {
		if (log.isDebugEnabled()) {
			log.debug("Handling Command: [{}] | Channel: '{}'.", getCommand().getLogText(), playerId);
		}

		// (4(Integer)) Data Length + (22(MoveIntentProto)) = 26 bytes
		if (payload.readableBytes() < 26) {
			log.error("Malformed '{}' Payload too short. Expected at least 26 bytes, but got {} bytes.", 
					this.getCommand().getLogText(), payload.readableBytes());
			return;
		}

		try {			
            final int dataLength = payload.readInt();
    		final ByteBuf protoData = payload.readBytes(dataLength);

			final MoveIntentProto intentProto = MoveIntentProto.parseFrom(protoData.nioBuffer());
			
			entityActionService.processMoveRequest(playerId, intentProto);
			
			protoData.release();
		} catch (Exception e) {
			log.error("Error processing '{}' command for playerId '{}': {}", 
					getCommand().getLogText(), playerId, e.getMessage(), e);
		}
	}
}
