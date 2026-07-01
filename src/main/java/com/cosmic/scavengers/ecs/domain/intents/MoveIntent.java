package com.cosmic.scavengers.ecs.domain.intents;

import com.cosmic.scavengers.ecs.domain.intents.meta.IEcsIntent;
import com.cosmic.scavengers.gameplay.queue.requests.MoveRequestData;

public record MoveIntent(
		long entityId,
		Long playerId,
		MoveRequestData requestData) implements IEcsIntent {	
}