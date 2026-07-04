package com.cosmic.scavengers.networking.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.networking.CommandRouter;
import com.cosmic.scavengers.networking.commands.CommandType;
import com.cosmic.scavengers.networking.queue.meta.INetworkingResponse;

/**
 * Network processor system. Polls responses queued by gameplay services and
 * sends them to clients via Netty. Runs on game thread as part of the game
 * loop.
 */
@Component
public class NetworkResponseProcessor implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(NetworkResponseProcessor.class);

	private final NetworkingResponseQueue requestQueue;
	private final CommandRouter commandRouter;

	public NetworkResponseProcessor(NetworkingResponseQueue responseQueue,
			CommandRouter commandRouter) {
		this.requestQueue = responseQueue;
		this.commandRouter = commandRouter;
	}

	@Override
	public void run() {
		processRequests();
	}

	private void processRequests() {
		while (requestQueue.hasRequest()) {
			INetworkingResponse request = requestQueue.poll();
			if (request == null) {
				log.warn("Polled 'null' Request from Networking Queue");
				continue;
			}

			try {
				processRequest(request);
			} catch (Exception e) {
				log.error("Error processing request {}", 
						request.getClass().getSimpleName(), e);
			}
		}
	}

	private void processRequest(INetworkingResponse request) {
		commandRouter.routeOutbound(request.getPlayerId(), CommandType.TYPE_BINARY, request.getCommand(), request.getMessage());		
	}
}
