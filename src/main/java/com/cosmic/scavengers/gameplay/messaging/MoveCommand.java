package com.cosmic.scavengers.gameplay.messaging;

import com.cosmic.scavengers.dominion.intents.MoveIntent;

/**
 * An immutable record representing a movement request. Using a record ensures
 * thread-safety and high performance.
 */
public record MoveCommand(
		long playerId, 
		long entityId, 
		MoveIntent payload
) implements IEcsCommand {
}