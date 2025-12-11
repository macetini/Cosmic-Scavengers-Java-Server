package com.cosmic.scavengers.networking.commands.handlers.binary;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.services.jooq.PlayerInitService;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommands;
import com.cosmic.scavengers.networking.commands.sender.MessageSender;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component
public class PlayerEntitiesCommandHandler implements ICommandBinaryHandler {
	private static final Logger log = LoggerFactory.getLogger(PlayerEntitiesCommandHandler.class);

	private final MessageSender messageSender;
	private final PlayerInitService playerInitService;

	public PlayerEntitiesCommandHandler(MessageSender messageSender, PlayerInitService playerInitService) {
		this.messageSender = messageSender;
		this.playerInitService = playerInitService;
	}

	@Override
	public NetworkBinaryCommands getCommand() {
		return NetworkBinaryCommands.REQUEST_PLAYER_ENTITIES_C;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, ByteBuf payload) {
		log.info("Handling {} command for channel {}.", getCommand().getLogName(), ctx.channel().id());

		Long playerId = payload.readLong();
		List<PlayerEntities> entites = playerInitService.getAllByPlayerId(playerId);

		log.info("Found {} entities for player ID {}.", entites.size(), playerId);
	}

}
