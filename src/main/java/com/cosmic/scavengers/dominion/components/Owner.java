package com.cosmic.scavengers.dominion.components;

/**
 * A component that identifies which player owns this entity. Used for security
 * checks and filtering logic in systems.
 */
public record Owner(long playerId) {
}