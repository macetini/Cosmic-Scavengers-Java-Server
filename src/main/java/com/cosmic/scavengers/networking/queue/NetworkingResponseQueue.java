package com.cosmic.scavengers.networking.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.cosmic.scavengers.networking.queue.meta.INetworkingResponse;

/**
 * Thread-safe queue for responses from game thread to Netty thread.
 * Gameplay services submit responses here.
 * NetworkProcessorSystem polls and sends them to clients.
 */
@Component
public class NetworkingResponseQueue {
	
	private final ConcurrentLinkedQueue<INetworkingResponse> requests = 
		new ConcurrentLinkedQueue<>();

	/**
	 * Submit a response to send to client.
	 * Thread-safe.
	 */
	public void submit(INetworkingResponse request) {
		requests.offer(request);
	}

	/**
	 * Poll a response on game thread.
	 */
	public INetworkingResponse poll() {
		return requests.poll();
	}

	/**
	 * Check if there are responses waiting.
	 */
	public boolean hasRequest() {
		return !requests.isEmpty();
	}

	/**
	 * Get current queue size.
	 */
	public int size() {
		return requests.size();
	}
}