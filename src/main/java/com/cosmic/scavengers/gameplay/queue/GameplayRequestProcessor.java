package com.cosmic.scavengers.gameplay.queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	// Direct O(1) class-to-handler lookup map
	private final Map<Class<? extends IGameplayRequest>, IGameplayRequestHandler<?>> handlerMap;

	public GameplayRequestProcessor(GameplayRequestQueue requestQueue, 
			List<IGameplayRequestHandler<?>> handlers) {
		this.requestQueue = requestQueue;
		this.handlerMap = new HashMap<>();

		// Map handlers by their supported request class at startup
		for (IGameplayRequestHandler<?> handler : handlers) {
			this.handlerMap.put(handler.getSupportedRequestType(), handler);
		}
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
				// Instantaneous O(1) lookup, no streams, no allocations
				IGameplayRequestHandler<?> handler = handlerMap.get(request.getClass());

				if (handler == null) {
					throw new IllegalStateException("No handler registered for request: " + request.getClass().getSimpleName());
				}

				dispatchToHandler(handler, request);
			} catch (Exception e) {
				log.error("Error processing request {}", request.getClass().getSimpleName(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends IGameplayRequest> void dispatchToHandler(IGameplayRequestHandler<?> handler, T request) {
		((IGameplayRequestHandler<T>) handler).handle(request);
	}
}
