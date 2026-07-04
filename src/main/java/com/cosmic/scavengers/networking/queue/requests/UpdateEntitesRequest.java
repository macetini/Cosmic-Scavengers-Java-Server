package com.cosmic.scavengers.networking.queue.requests;

import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.queue.meta.INetworkingRequest;
import com.cosmicscavengers.networking.protobuf.entities.EntitySyncResponse;
import com.google.protobuf.GeneratedMessage;

import io.netty.channel.ChannelId;

public record UpdateEntitesRequest(Long playerId, EntitySyncResponse response) implements INetworkingRequest {

	@Override
	public Long getPlayerId() {
		return playerId;
	}
	
	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_PLAYER_ENTITIES_S;
	}

	@Override
	public GeneratedMessage getMessage() {			
		return response;
	}
}
