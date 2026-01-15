package com.cosmic.scavengers.gameplay.messaging;

/**
 * The base contract for any action that modifies the Dominion world. Permits
 * ensures the compiler knows exactly which records exist.
 */
public sealed interface IEcsCommand permits MoveCommand {
	long playerId();
	long entityId();
}