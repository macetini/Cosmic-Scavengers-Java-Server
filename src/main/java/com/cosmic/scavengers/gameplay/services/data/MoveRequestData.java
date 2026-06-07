package com.cosmic.scavengers.gameplay.services.data;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale4f;

import com.cosmic.scavengers.ecs.math.Vector3Scaled;

/**
 * Record class that holds the data that describes a move request.
 */
public record MoveRequestData(
        long entityId,
        Long playerId,
        Vector3Scaled target,
        long movementSpeed, 
        long rotationSpeed, 
        long stoppingDistance) {
}
