package com.cosmic.scavengers.utils;

import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.Scale4f;

/**
 * Utility class to manage fixed-point number conversions using Decimal4j. The
 * game uses Scale4f (S4) for 4 decimal places of precision, ensuring
 * deterministic results across all clients and the server. * Naming matches the
 * C# DeterministicUtils for cross-platform symmetry.
 */
public final class DecimalUtils {

	/**
	 * Standard arithmetic settings for the game engine. Uses HALF_EVEN (Banker's
	 * Rounding) for consistent results.
	 */
	public static final DecimalArithmetic ARITHMETIC = Scale4f.INSTANCE.getArithmetic(RoundingMode.HALF_EVEN);

	/**
	 * Factory for creating Scale4f decimal instances.
	 */
	public static final DecimalFactory<Scale4f> FACTORY = Factories.getDecimalFactory(Scale4f.INSTANCE);

	private DecimalUtils() {
		// Prevent instantiation
		throw new AssertionError("DecimalUtils cannot be instantiated");
	}

	/**
	 * Converts a scaled long value from the network/Protobuf (Value * 10,000) into
	 * a high-precision Decimal4j object. Symmetrical with C#
	 * DeterministicUtils.ToScaled(float)
	 */
	public static Decimal<Scale4f> fromScaled(long scaledValue) {
		return FACTORY.valueOfUnscaled(scaledValue);
	}

	/**
	 * Converts a Decimal4j object back into a scaled long value suitable for
	 * network packets or database storage (Value * 10,000). Symmetrical with C#
	 * DeterministicUtils.FromScaled(long)
	 */
	public static long toScaled(Decimal<Scale4f> decimal) {
		return decimal.unscaledValue();
	}

	/**
	 * Creates a Decimal4j object from a standard double. Warning: Should be used
	 * sparingly (e.g., config loading) as doubles introduce non-determinism.
	 */
	public static Decimal<Scale4f> fromDouble(double value) {
		return FACTORY.valueOf(value);
	}

	/**
	 * Creates a Decimal4j object from a float. Useful for bridging legacy database
	 * fields to the deterministic engine.
	 */
	public static Decimal<Scale4f> fromFloat(float value) {
		return FACTORY.valueOf(value);
	}

	/**
	 * Creates a Decimal4j object from a whole number (long integer). Example:
	 * fromInteger(5) -> 5.0000
	 */
	public static Decimal<Scale4f> fromLong(long value) {
		return FACTORY.valueOf(value);
	}

	/**
	 * Alias for fromScaled to maintain compatibility with standard Decimal4j
	 * terminology.
	 */
	public static Decimal<Scale4f> fromUnscaled(long unscaledValue) {
		return FACTORY.valueOfUnscaled(unscaledValue);
	}

	/**
	 * Alias for toScaled to maintain compatibility with standard Decimal4j
	 * terminology.
	 */
	public static long toUnscaled(Decimal<Scale4f> decimal) {
		return decimal.unscaledValue();
	}
}