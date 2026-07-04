package com.cosmic.scavengers.gameplay.queue.requests;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;

public record GetWorldDataRequest(Long playerId) implements IGameplayRequest {

	@Override
	public Long getPlayerId() {		
		return playerId;
	}
}