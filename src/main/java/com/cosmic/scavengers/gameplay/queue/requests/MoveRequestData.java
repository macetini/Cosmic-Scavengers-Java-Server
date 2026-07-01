package com.cosmic.scavengers.gameplay.queue.requests;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.networking.math.Vector3Long;

/**
 * Record class that holds the data that describes a move request.
 */
public record MoveRequestData(
		Long playerId,
        Long entityId,
        Vector3Long target) implements IGameplayRequest {
}
