package com.cosmic.scavengers.core.commands;

import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface ICommandBinaryHandler {
	/**
	 * Specifies which command this handler is responsible for.
	 */
	NetworkBinaryCommand getCommand();	
	
	void handle(ChannelHandlerContext ctx, ByteBuf payload);
}