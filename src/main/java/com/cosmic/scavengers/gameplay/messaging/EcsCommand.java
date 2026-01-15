package com.cosmic.scavengers.gameplay.messaging;

/**
 * The base contract for any action that modifies the Dominion world. Permits
 * ensures the compiler knows exactly which records exist.
 */
public class EcsCommand {
	private long playerId;
	private long entityId;
	private EcsIntent payload;

	public EcsCommand(long playerId, long entityId, EcsIntent payload) {
		this.playerId = playerId;
		this.entityId = entityId;
		this.payload = payload;
	}

	public long playerId() {
		return playerId;
	}

	public long entityId() {
		return entityId;
	}

	public EcsIntent payload() {
		return payload;
	}
}