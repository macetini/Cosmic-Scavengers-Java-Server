package com.cosmic.scavengers.ecs.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.ecs.commands.meta.IEcsCommand;
import com.cosmic.scavengers.gameplay.registries.EntityRegistry;

import dev.dominion.ecs.api.Dominion;

@Component
public class EcsCommandQueueProcessing implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(EcsCommandQueueProcessing.class);

	private final EcsCommandQueue commandQueue;	//
	private final Dominion dominion;
	
	public EcsCommandQueueProcessing(
			EcsCommandQueue 
			commandQueue, Dominion dominion) {
		this.commandQueue = commandQueue;
		this.dominion = dominion;
	}

	@Override
	public void run() {
		while (!commandQueue.isEmpty()) {
			IEcsCommand command = commandQueue.poll();
			if (command == null) {
				log.error("Null command found in ECS Command Queue.");
				continue;
			}

			try {
				command.execute(dominion);
			} catch (Exception e) {
				log.error("Failed to execute command: " + command.getClass().getSimpleName(), e);
			}
		}
	}
}