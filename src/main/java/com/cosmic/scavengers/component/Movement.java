package com.cosmic.scavengers.component;

import com.cosmic.scavengers.util.meta.GameDecimal;

/**
 * Component defining an entity's movement goal and movement speed.
 *
 * This component is used by the MovementSystem to update the Position.
 */
public record Movement(GameDecimal targetX, GameDecimal targetY, GameDecimal speed) {
}