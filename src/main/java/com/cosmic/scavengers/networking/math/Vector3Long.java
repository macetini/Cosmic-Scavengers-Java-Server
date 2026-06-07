package com.cosmic.scavengers.networking.math;

/**
 * An immutable 3D vector. Perfect for server-side state where data shouldn't be
 * mutated accidentally.
 */
public record Vector3Long(long x, long y, long z) {
	public static final Vector3Long ZERO = new Vector3Long(0L, 0L, 0L);
}
