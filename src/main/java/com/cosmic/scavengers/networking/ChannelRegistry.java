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

	private final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channels = new ConcurrentHashMap<>();

	public void add(ChannelHandlerContext ctx) {
		log.trace("Adding channel {} to registry.", ctx.channel().id());
		channels.put(ctx.channel().id(), ctx);
	}

	public void remove(ChannelHandlerContext ctx) {
		log.trace("Removing channel {} from registry.", ctx.channel().id());
		channels.remove(ctx.channel().id());
	}

	public ChannelHandlerContext get(ChannelId id) {
		if (!channels.containsKey(id)) {
			log.warn("Channel {} not found in registry.", id);
		}
		return channels.get(id);
	}
}
