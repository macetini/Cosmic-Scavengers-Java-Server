package com.cosmic.scavengers.gameplay.queue.requests;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;

import io.netty.channel.ChannelId;

public record GetAllPlayerEntitiesRequest(Long playerId) implements IGameplayRequest {

	@Override
	public Long getPlayerId() {
		return playerId;
	}
}
