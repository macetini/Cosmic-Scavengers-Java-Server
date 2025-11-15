package com.cosmic.scavengers.system;

import static com.cosmic.scavengers.util.DecimalUtils.ARITHMETIC;
import static com.cosmic.scavengers.util.DecimalUtils.FACTORY;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosmic.scavengers.component.Movement;
import com.cosmic.scavengers.component.Position;
import com.cosmic.scavengers.util.DecimalUtils;
import com.cosmic.scavengers.util.meta.GameDecimal;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;

/**
 * Handles the movement of all entities. Implements Runnable and queries the
 * Dominion context directly, which is the correct pattern for systems in the
 * modern Dominion ECS API.
 */
public class MovementSystem implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(MovementSystem.class);

	// Time delta for fixed-point math (0.1 seconds per tick)
	private static final Decimal<Scale4f> TICK_DELTA = DecimalUtils.fromDouble(0.1);
	// Distance threshold for snapping to target
	private static final Decimal<Scale4f> THRESHOLD = DecimalUtils.fromDouble(0.0001);

	// Precomputed threshold squared in unscaled long form
	// To ensure 100% determinism and API compatibility, using ARITHMETIC instance
	// instead of power function
	private static final long THRESHOLD_SQUARED_UNSCALED = ARITHMETIC.multiply(THRESHOLD.unscaledValue(),
			THRESHOLD.unscaledValue());

	private final Dominion dominion;

	public MovementSystem(Dominion dominion) {
		this.dominion = dominion;
	}

	@Override
	public void run() {
		// Query entities with both Position and Movement components
		dominion.findEntitiesWith(Position.class, Movement.class).stream().forEach(result -> {
			final Entity entity = result.entity();
			final Position position = result.comp1();
			final Movement movement = result.comp2();

			GameDecimal positionX = position.x();			
			GameDecimal positionY = position.y();			

			GameDecimal movementTargetX = movement.targetX();
			GameDecimal movementTargetY = movement.targetY();			

			// --- Calculate Distance to Target (using unscaled long values) ---

			// Delta = Target - Current Position
			long deltaXUnscaled = ARITHMETIC.subtract(movementTargetX.unscaledValue(), positionX.unscaledValue());
			long deltaYUnscaled = ARITHMETIC.subtract(movementTargetY.unscaledValue(), positionY.unscaledValue());

			// Distance Squared = (DeltaX^2) + (DeltaY^2)
			long deltaXSquaredUnscaled = ARITHMETIC.multiply(deltaXUnscaled, deltaXUnscaled);
			long deltaYSquaredUnscaled = ARITHMETIC.multiply(deltaYUnscaled, deltaYUnscaled);

			long distanceSquaredUnscaled = ARITHMETIC.add(deltaXSquaredUnscaled, deltaYSquaredUnscaled);

			// --- Check for Target Reached (Snap) ---

			if (distanceSquaredUnscaled <= THRESHOLD_SQUARED_UNSCALED) {
				// Reached target: snap Position and remove Movement component
				Position finalPosition = new Position(movementTargetX, movementTargetY);

				// Update entity
				entity.add(finalPosition);
				entity.removeType(Movement.class);

				log.debug("Entity {} reached target ({}, {}). Movement stopped.", entity.getName(), movement.targetX(),
						movement.targetY());
				return;
			}

			// --- Calculate Normalized Direction and Displacement ---

			// Actual Distance = Sqrt(Distance Squared)
			long distanceUnscaled = ARITHMETIC.sqrt(distanceSquaredUnscaled);

			// Normalized Direction Vector (Delta / Distance)
			// Cast to GameDecimal is needed here as FACTORY.valueOf returns
			// Decimal<Scale4f>
			GameDecimal normX = (GameDecimal) FACTORY.valueOf(ARITHMETIC.divide(deltaXUnscaled, distanceUnscaled));
			GameDecimal normY = (GameDecimal) FACTORY.valueOf(ARITHMETIC.divide(deltaYUnscaled, distanceUnscaled));

			// Displacement Magnitude = Speed * Time Delta
			long displacementUnscaled = ARITHMETIC.multiply(movement.speed().unscaledValue(),
					TICK_DELTA.unscaledValue());

			// --- Calculate New Position ---

			// Displacement Vector (Norm * Displacement Magnitude)
			long dispXUnscaled = ARITHMETIC.multiply(normX.unscaledValue(), displacementUnscaled);
			long dispYUnscaled = ARITHMETIC.multiply(normY.unscaledValue(), displacementUnscaled);

			// New Position = Current Position + Displacement Vector
			GameDecimal newX = (GameDecimal) FACTORY
					.valueOf(ARITHMETIC.add(position.x().unscaledValue(), dispXUnscaled));
			GameDecimal newY = (GameDecimal) FACTORY
					.valueOf(ARITHMETIC.add(position.y().unscaledValue(), dispYUnscaled));

			// Update the entity's position component
			entity.add(new Position(newX, newY));
			log.debug("Entity {} moved to ({}, {})", entity.getName(), newX, newY);

		});
	}
}
