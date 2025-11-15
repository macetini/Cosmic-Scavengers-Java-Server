package com.cosmic.scavengers.util.meta;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale4f;

/**
 * Type alias for the fixed-point Decimal type used in the game simulation. This
 * must be a top-level interface to ensure the Java compiler correctly treats it
 * as equivalent to Decimal<Scale4f>.
 */
public interface GameDecimal extends Decimal<Scale4f> {
}