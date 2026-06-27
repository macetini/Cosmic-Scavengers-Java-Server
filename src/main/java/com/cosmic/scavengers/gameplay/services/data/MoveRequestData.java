package com.cosmic.scavengers.gameplay.services.data;

import com.cosmic.scavengers.networking.math.Vector3Long;

/**
 * Record class that holds the data that describes a move request.
 */
public record MoveRequestData(
		Long playerId,
        Long entityId,
        Vector3Long target        
        //long movementSpeed, 
        //long rotationSpeed, 
        //long stoppingDistance
        ) {
}
