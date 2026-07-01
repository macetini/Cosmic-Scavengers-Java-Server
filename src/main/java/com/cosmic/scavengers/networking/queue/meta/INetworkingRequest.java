package com.cosmic.scavengers.networking.queue.meta;

import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.google.protobuf.GeneratedMessage;

import io.netty.channel.ChannelId;

public interface INetworkingRequest {
	
	/**
	 * Get the channel context to send responses to client.
	 */
	ChannelId getChannelId();
	
	NetworkBinaryCommand getCommand();
	
	GeneratedMessage getMessage();
}
