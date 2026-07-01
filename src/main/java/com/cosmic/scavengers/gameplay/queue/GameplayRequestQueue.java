package com.cosmic.scavengers.gameplay.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.cosmic.scavengers.gameplay.queue.meta.IGameplayRequest;

/**
 * Thread-safe queue for transferring requests from Netty thread to game thread.
 */
@Component
public class GameplayRequestQueue {

	private final ConcurrentLinkedQueue<IGameplayRequest> requests = new ConcurrentLinkedQueue<>();

	/**
	 * Submit a request from Netty thread.
	 */
	public void submit(IGameplayRequest request) {
		requests.offer(request);
	}

	/**
	 * Poll a request on game thread.
	 */
	public IGameplayRequest poll() {
		return requests.poll();
	}

	/**
	 * Check if there are requests waiting.
	 */
	public boolean hasRequests() {
		return !requests.isEmpty();
	}

	/**
	 * Get current queue size.
	 */
	public int size() {
		return requests.size();
	}
}
