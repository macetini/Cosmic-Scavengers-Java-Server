package com.cosmic.scavengers.gameplay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.ecs.queue.EcsCommandQueue;
import com.cosmic.scavengers.networking.math.Vector3Long;

@Service
public class EntityActionService {
	private static final Logger log = LoggerFactory.getLogger(EntityActionService.class);
	
	private final EcsCommandQueue dominionCommandQueue;	

	public EntityActionService(EcsCommandQueue dominionCommandQueue) {
		this.dominionCommandQueue = dominionCommandQueue;
	}

	/**
	 * Authoritatively processes a move intent.
	 */
	@Transactional
	public void processMoveRequest(long entityId, Vector3Long target) {
		if (log.isDebugEnabled()) {
			log.debug("Processing Move Intent for Entity ID: '{}' to Target: [X:{}, Y:{}, Z:{}]", 
					entityId,
					target.x(), target.y(), target.z());					
		}
		//final MoveRequestData data = new MoveRequestData(entityId, target);
		
		//final MoveEntityCommand command = new MoveEntityCommand()
		//dominionCommandQueue.submit(command);
	}
}
