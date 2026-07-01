package com.cosmic.scavengers.gameplay.queue;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;
import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequestHandler;

/**
 * Generic gameplay request processor. Polls requests and delegates to
 * appropriate handlers. Handlers are injected and responsible for their own
 * logic.
 */
@Component
public class GameplayRequestProcessor implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(GameplayRequestProcessor.class);

	private final GameplayRequestQueue requestQueue;
	private final List<IGameplayRequestHandler<?>> handlers;

	public GameplayRequestProcessor(GameplayRequestQueue requestQueue, List<IGameplayRequestHandler<?>> handlers) {
		this.requestQueue = requestQueue;
		this.handlers = handlers;
	}

	@Override
	public void run() {
		processRequests();
	}

	private void processRequests() {
		while (requestQueue.hasRequests()) {
			IGameplayRequest request = requestQueue.poll();
			if (request == null) {
				log.warn("Polled 'null' Request from Gameplay Queue.");
				continue;
			}

			try {			
				IGameplayRequestHandler<?> handler = handlers
						.stream()
						.filter(h -> h.canHandle(request))
						.findFirst()
						.orElseThrow(() -> new IllegalStateException(
								"No handler for request: " + request.getClass().getSimpleName()));

				dispatchToHandler(handler, request);
			} catch (Exception e) {
				log.error("Error processing request {}", 
						request.getClass().getSimpleName(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends IGameplayRequest> void dispatchToHandler(IGameplayRequestHandler<?> handler, T request) {
		((IGameplayRequestHandler<T>) handler).handle(request);
	}
}
