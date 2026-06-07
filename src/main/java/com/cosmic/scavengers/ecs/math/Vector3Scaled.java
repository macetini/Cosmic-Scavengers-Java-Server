package com.cosmic.scavengers.ecs.math;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.scale.Scale4f;

import com.cosmic.scavengers.core.utils.DecimalUtil;

public record Vector3Scaled(
		Decimal<Scale4f> x, 
		Decimal<Scale4f> y, 
		Decimal<Scale4f> z) {
	
	public static final Vector3Scaled ZERO = new Vector3Scaled(
			Decimal4f.ZERO, 
			Decimal4f.ZERO, 
			Decimal4f.ZERO);

	public static final Vector3Scaled fromScaled(long scaledX, long scaledY, long scaledZ) {
		return new Vector3Scaled(
				DecimalUtil.fromScaled(scaledX), 
				DecimalUtil.fromScaled(scaledY), 
				DecimalUtil.fromScaled(scaledZ));
	}
}
