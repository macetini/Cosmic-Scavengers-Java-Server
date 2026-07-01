package com.cosmic.scavengers.gameplay.queue.meta;

/**
 * Generic handler for any gameplay request. Each handler is responsible for its
 * own logic and dependencies.
 */
public interface IGameplayRequestHandler<T extends IGameplayRequest> {

	/**
	 * Check if this handler can handle the request type.
	 */
	boolean canHandle(IGameplayRequest request);

	/**
	 * Handle the request.
	 */
	void handle(T request);
}
