package com.cosmic.scavengers.core.commands;

import com.cosmic.scavengers.networking.commands.NetworkTextCommand;

import io.netty.channel.ChannelHandlerContext;

public interface ICommandTextHandler {
	/**
	 * Specifies which command this handler is responsible for.
	 */
	NetworkTextCommand getCommand();
	
	void handle(ChannelHandlerContext ctx, String[] payload);
}