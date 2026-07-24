package com.cosmic.scavengers.networking.queue.responses;

import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.queue.meta.INetworkingResponse;
import com.cosmicscavengers.networking.protobuf.worlddata.WorldDataOuterClass.WorldData;
import com.google.protobuf.GeneratedMessage;

public record GetWorldDataResponse(Long playerId, WorldData response) implements INetworkingResponse {

	@Override
	public Long getPlayerId() {
		return playerId;
	}

	@Override
	public NetworkBinaryCommand getCommand() {
		return NetworkBinaryCommand.REQUEST_WORLD_STATE_S;
	}

	@Override
	public GeneratedMessage getMessage() {
		return response;
	}
}