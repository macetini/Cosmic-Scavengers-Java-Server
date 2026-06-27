package com.cosmic.scavengers.gameplay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.ecs.commands.MoveEntityCommand;
import com.cosmic.scavengers.ecs.queue.EcsCommandQueue;
import com.cosmic.scavengers.gameplay.services.data.MoveRequestData;
import com.cosmic.scavengers.networking.math.Vector3Long;
import com.cosmic.scavengers.networking.proto.traits.MoveIntentProto;
import com.cosmic.scavengers.networking.proto.traits.MoveTargetProto;

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
	public void processMoveRequest(Long playerId, MoveIntentProto intentProto) {
		log.debug("Processing Move Intent for Player ID: '{}'", playerId);		
		
		MoveTargetProto moveTargetProto = intentProto.getRequestData();
		
		Long entityId = intentProto.getEntityId();
		
		Vector3Long target = new Vector3Long(
				moveTargetProto.getTargetX(),
				moveTargetProto.getTargetY(),
				moveTargetProto.getTargetZ());
				
		log.trace("Adding Move Intent to Entity ID: '{}' to Target: [X:{}, Y:{}, Z:{}]", 
				entityId, 
				target.x(), target.y(), target.z());				
		
		final MoveRequestData data = new MoveRequestData(playerId, entityId, target);
		final MoveEntityCommand command = new MoveEntityCommand(data);
		dominionCommandQueue.submit(command);
	}
}
