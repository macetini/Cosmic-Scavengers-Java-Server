package com.cosmic.scavengers.system;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.dominion.components.Movement;
import com.cosmic.scavengers.dominion.intents.MoveIntent;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;

@Component
public class IntentProcessorSystem implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(IntentProcessorSystem.class);
	private final Dominion dominion;

	public IntentProcessorSystem(Dominion dominion) {
		this.dominion = dominion;
	}

	@Override
	public void run() {
		// 1. Collect all entities that need a component swap
		List<EntityUpdate> updates = new ArrayList<>();

		dominion.findEntitiesWith(MoveIntent.class).stream().forEach(result -> {
			updates.add(new EntityUpdate(result.entity(), result.comp()));
		});

		// 2. The stream is now closed. Execute changes.
		for (EntityUpdate update : updates) {
			log.info("Entity update");
			
			Entity entity = update.entity;
			MoveIntent intent = update.intent;

			// Remove intent first
			entity.remove(intent);

			// If it's already moving, remove the old movement to avoid the "Duplicate"
			// error
			if (entity.has(Movement.class)) {
				entity.remove(Movement.class);
			}

			// Now add the new Movement
			// entity.add(new Movement(intent.targetX(), intent.targetY(), intent.targetZ(),
			// intent.speed()));
		}
	}

	// Small helper record to hold data outside the stream
	private record EntityUpdate(Entity entity, MoveIntent intent) {
	}
}