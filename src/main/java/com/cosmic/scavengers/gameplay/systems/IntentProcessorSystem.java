package com.cosmic.scavengers.gameplay.systems;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.utils.DecimalUtil;
import com.cosmic.scavengers.ecs.domain.components.Movement;
import com.cosmic.scavengers.ecs.domain.intents.MoveIntent;
import com.cosmic.scavengers.gameplay.queue.requests.MoveRequestData;
import com.cosmic.scavengers.networking.math.Vector3Long;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;

/**
 * The "Gatekeeper" System. Converts transient player MoveIntents into
 * persistent Movement states.
 */
@Component
public class IntentProcessorSystem implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(IntentProcessorSystem.class);

	private final Dominion dominion;

	public IntentProcessorSystem(Dominion dominion) {
		this.dominion = dominion;
	}

	@Override
	public void run() {
		dominion.findEntitiesWith(MoveIntent.class).stream().forEach(result -> {
			Entity entity = result.entity();
			MoveIntent intent = result.comp();

			MoveRequestData requestData = intent.requestData();

			log.debug("Processing Move Intent for Player '{}' Entity: '{}': Moving to [{},{},{}]", requestData.playerId(),
					requestData.entityId(), requestData.target().x(), requestData.target().y(), requestData.target().z());
			
			Vector3Long target = requestData.target();

			// Convert Vector3Long to Decimal
			Decimal<Scale4f> targetX = DecimalUtil.fromScaled(target.x());
			Decimal<Scale4f> targetY = DecimalUtil.fromScaled(target.y());
			Decimal<Scale4f> targetZ = DecimalUtil.fromScaled(target.z());

			// Default movement speed - adjust as needed
			//Decimal<Scale4f> speed = Decimal.valueOf(100, Scale4f.INSTANCE);

			//Movement movementState = new Movement(targetX, targetY, targetZ, speed);

			//entity.remove(intent);
			//entity.add(movementState);
		});
	}
}
