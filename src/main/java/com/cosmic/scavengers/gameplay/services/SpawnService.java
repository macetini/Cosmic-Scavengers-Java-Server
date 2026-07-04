package com.cosmic.scavengers.gameplay.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.gameplay.factories.PlayerEntityProtoFactory;
import com.cosmic.scavengers.networking.queue.NetworkingResponseQueue;
import com.cosmic.scavengers.networking.queue.responses.UpdateEntitesResponse;
import com.cosmicscavengers.networking.protobuf.entities.EntitySyncResponse;
import com.cosmicscavengers.networking.protobuf.entities.PlayerEntityProto;

import io.netty.channel.ChannelId;

/**
 * Service for spawning player entities.
 * Coordinates between gameplay and ECS layers.
 */
@Service
public class SpawnService {
	private static final Logger log = LoggerFactory.getLogger(SpawnService.class);

	private final NetworkingResponseQueue networkingRequestQueue; 
	private final PlayerEntityProtoFactory protoFactory;

	public SpawnService(NetworkingResponseQueue networkingRequestQueue,
			PlayerEntityProtoFactory protoFactory) {
		this.networkingRequestQueue = networkingRequestQueue;
		this.protoFactory = protoFactory;		
	}

	/**
	 * Spawn player entities.
	 * Queues the spawn command to ECS.
	 */
	public void spawnEntities(Long playerId, final List<PlayerEntities> playerEntities) {
		List<PlayerEntityProto> entitiesProtos = playerEntities
				.stream()
				.map(protoFactory::build)
				.toList();
		
		EntitySyncResponse.Builder responseBuilder = EntitySyncResponse.newBuilder();
		entitiesProtos.forEach(responseBuilder::addEntities);
		
		PlayerEntities firstEntity = playerEntities.get(0);
		responseBuilder.setWorldId(firstEntity.getWorldId());
		responseBuilder.setSectorId(firstEntity.getSectorId());
		
		EntitySyncResponse finalMessage = responseBuilder.build();

		UpdateEntitesResponse spawnRequest = new UpdateEntitesResponse(playerId, finalMessage);
		networkingRequestQueue.submit(spawnRequest);
		log.debug("Spawn request queued for player {} with {} entities.", playerId, entitiesProtos.size());
	}
}