package com.cosmic.scavengers.core.commands;

import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;

import io.netty.buffer.ByteBuf;

public interface ICommandBinaryHandler {
	/**
	 * Specifies which command this handler is responsible for.
	 */
	NetworkBinaryCommand getCommand();	
	
	void handle(Long playerId, ByteBuf payload);
}