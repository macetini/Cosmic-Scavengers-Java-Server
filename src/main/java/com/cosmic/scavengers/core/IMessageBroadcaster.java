package com.cosmic.scavengers.core;

import com.cosmic.scavengers.networking.GameChannelHandler;

/**
 * Defines the contract that the GameEngine needs for outbound communication.
 * This pattern is used to break the circular dependency between the GameEngine
 * and the NettyServerInitializer in the Spring application context.
 */
public interface IMessageBroadcaster {

	/**
	 * Broadcasts a message to all connected clients except the original sender.
	 * * @param message The message content to be sent.
	 * 
	 * @param sender The handler (client connection) that originated the message,
	 *               which should be excluded from the broadcast.
	 */
	void broadcast(String message, GameChannelHandler sender);
}