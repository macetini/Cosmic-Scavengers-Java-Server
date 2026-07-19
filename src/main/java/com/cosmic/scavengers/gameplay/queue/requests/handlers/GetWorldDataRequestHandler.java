package com.cosmic.scavengers.gameplay.queue.requests.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;
import com.cosmic.scavengers.gameplay.queue.requests.GetWorldDataRequest;

@Component
public class GetWorldDataRequestHandler implements IGameplayRequestHandler<GetWorldDataRequest> {
	private static final Logger log = LoggerFactory.getLogger(GetWorldDataRequestHandler.class);
	
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
		log.info("Handling GetWorldDataRequest for player ID: {}", request.playerId());
		
	}
}
