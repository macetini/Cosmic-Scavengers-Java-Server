package com.cosmic.scavengers.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.dominion.components.Owner;
import com.cosmic.scavengers.dominion.intents.meta.EcsIntent;
import com.cosmic.scavengers.dominion.messaging.EcsCommand;
import com.cosmic.scavengers.dominion.messaging.EcsCommandQueue;
import com.cosmic.scavengers.gameplay.registry.EntityRegistry;

import dev.dominion.ecs.api.Entity;

@Component
public class EcsCommandQueueProcessing implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(EcsCommandQueueProcessing.class);

	private final EcsCommandQueue commandQueue;
	private final EntityRegistry entityRegistry;

	public EcsCommandQueueProcessing(EcsCommandQueue commandQueue, EntityRegistry entityRegistry) {
		this.commandQueue = commandQueue;
		this.entityRegistry = entityRegistry;
	}

	@Override
	public void run() {
		// Drain the queue at the start of every tick
		while (!commandQueue.isEmpty()) {
			EcsCommand command = commandQueue.poll();
			if (command == null) {
				log.warn("Received null command.");
				continue;
			}

			Entity entity = entityRegistry.getLiveEntity(command.entityId());
			if (entity == null) {
				log.warn("Command discarded: Entity Id: {} is no longer live.", command.entityId());
				continue;
			}

			if (!validateOwnership(entity, command.playerId())) {
				log.warn("Unauthorized command! Player {} tried to control Entity Name: {}.", command.playerId(),
						entity.getName());
				continue;
			}

			EcsIntent payload = command.payload();
			Class<?> payloadClass = payload.getClass();

			if (entity.has(payloadClass)) {
				Object existing = entity.get(payloadClass);
				if (existing != null) {
					entity.remove(existing);
				}
			}

			entity.add(payload);
		}
	}

	private boolean validateOwnership(Entity entity, long playerId) {
		Owner owner = entity.get(Owner.class);
		if (owner == null || owner.playerId() != playerId) {
			log.warn("Unauthorized command! Player {} tried to control Entity {}.", playerId, entity);
			return false;
		}
		return true;
	}
}