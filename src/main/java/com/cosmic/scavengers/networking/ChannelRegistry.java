package com.cosmic.scavengers.networking;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

@Component
public class ChannelRegistry {
	private static final Logger log = LoggerFactory.getLogger(ChannelRegistry.class);

	private final ConcurrentHashMap<Long, ChannelHandlerContext> channels = new ConcurrentHashMap<>();

	public void add(Long playerId, ChannelHandlerContext ctx) {
		log.trace("Adding channel {} to registry.", ctx.channel().id());
		channels.put(playerId, ctx);
	}

	public void remove(ChannelHandlerContext ctx) {
		log.trace("Removing channel {} from registry.", ctx.channel().id());
		channels.values().removeIf(existingCtx -> existingCtx.channel().id().equals(ctx.channel().id()));
	}

	public ChannelHandlerContext get(Long playerId) {
		if (!channels.containsKey(playerId)) {
			log.warn("Channel for Player Id '{}' not found in registry.", playerId);
		}
		return channels.get(playerId);
	}
}
