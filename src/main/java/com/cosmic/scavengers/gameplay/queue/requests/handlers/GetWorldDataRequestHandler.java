package com.cosmic.scavengers.gameplay.queue.requests.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.executor.DatabaseExecutor;
import com.cosmic.scavengers.db.services.PlayerInitService;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetWorldDataRequest;
import com.cosmic.scavengers.networking.queue.NetworkingResponseQueue;
import com.cosmic.scavengers.networking.queue.responses.GetWorldDataResponse;
import com.cosmicscavengers.networking.protobuf.worlddata.WorldDataOuterClass.WorldData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GetWorldDataRequestHandler implements IGameplayRequestHandler<GetWorldDataRequest> {
	private static final Logger log = LoggerFactory.getLogger(GetWorldDataRequestHandler.class);

	private final DatabaseExecutor dbExecutor;
	private final PlayerInitService playerInitService;
	private final NetworkingResponseQueue responseQueue;
	private final ObjectMapper objectMapper;

	public GetWorldDataRequestHandler(DatabaseExecutor dbExecutor, PlayerInitService playerInitService,
			NetworkingResponseQueue responseQueue, ObjectMapper objectMapper) {
		this.dbExecutor = dbExecutor;
		this.playerInitService = playerInitService;
		this.responseQueue = responseQueue;
		this.objectMapper = objectMapper;
	}

	@Override
	public Class<GetWorldDataRequest> getSupportedRequestType() {
		return GetWorldDataRequest.class;
	}

	@Override
	public boolean canHandle(IGameplayRequest request) {
		return request instanceof GetWorldDataRequest;
	}

	@Override
	public void handle(GetWorldDataRequest request) {
		long playerId = request.playerId();
		log.info("Handling GetWorldDataRequest for player ID: {}", playerId);

		dbExecutor.execute(() -> {
			try {
				final var worldDefinition = playerInitService.getCurrentWorldDataByPlayerId(playerId);

				String configJson = objectMapper.writeValueAsString(worldDefinition.getConfig());

				WorldData protoMessage = WorldData.newBuilder()
						.setId(worldDefinition.getId())
						.setConfigJson(configJson).build();

				responseQueue.submit(new GetWorldDataResponse(playerId, protoMessage));
				log.debug("Submitted GetWorldDataResponse for Player ID: '{}'", playerId);
			} catch (Exception e) {
				log.error("Error fetching world data for player ID {}: {}", playerId, e.getMessage(), e);
			}
		});
	}
}
