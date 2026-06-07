package com.cosmic.scavengers.networking.handlers.binary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.gameplay.services.EntityActionService;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.math.Vector3Long;
import com.cosmic.scavengers.networking.proto.traits.MoveIntentProto;
import com.cosmic.scavengers.networking.proto.traits.MoveTargetProto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component
public class EntityMoveCommandHandler implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(EntityMoveCommandHandler.class);

	private final EntityActionService entityActionService;

	public EntityMoveCommandHandler(EntityActionService entityActionService) {
		this.entityActionService = entityActionService;
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_ENTITY_MOVE_C;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, ByteBuf payload) {
		log.info("Handling {} command for channel {}.", getCommand().getLogText(), ctx.channel().id());

		// (4(Integer)) Data Length + (22(MoveIntentProto)) = 26 bytes
		if (payload.readableBytes() < 26) {
			log.error("Malformed move command from Chanel Id '{}': expected 26 bytes, got {}", 
					ctx.channel().id(), payload.readableBytes());
			return;
		}

		final int dataLength = payload.readInt();
		final ByteBuf protoData = payload.readBytes(dataLength);
		try {
			final MoveIntentProto intentProto = MoveIntentProto.parseFrom(protoData.nioBuffer());
			
			long entityId = intentProto.getEntityId();
			
			MoveTargetProto moveTargetProto = intentProto.getRequestData();
			Vector3Long targetScaled = new Vector3Long(
					moveTargetProto.getTargetX(),
					moveTargetProto.getTargetY(),
					moveTargetProto.getTargetZ());

			entityActionService.processMoveRequest(entityId, targetScaled);
		} catch (Exception e) {
			log.error("Malformed move command from Channel Id '{}': {}", ctx.channel().id(), e.getMessage());
		} finally {
			protoData.release();
		}
	}
}
