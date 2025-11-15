package com.cosmic.scavengers.component;

import com.cosmic.scavengers.util.meta.GameDecimal;

/**
 * Component defining the entity's current location in the game world.
 *
 * Uses Decimal<?> for deterministic fixed-point arithmetic. The specific scale
 * (e.g., Decimal<S4>) must be chosen when instantiated to match the precision
 * agreed upon by the server and client.
 */
public record Position(GameDecimal x, GameDecimal y) {
}