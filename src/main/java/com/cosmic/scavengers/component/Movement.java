package com.cosmic.scavengers.component;

import org.decimal4j.api.Decimal;

/**
 * Component defining an entity's movement goal and movement speed.
 *
 * This component is used by the MovementSystem to update the Position.
 */
public record Movement(Decimal<?> targetX, Decimal<?> targetY, Decimal<?> speed) {
}