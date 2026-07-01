package com.cosmic.scavengers.networking.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.cosmic.scavengers.networking.queue.meta.INetworkingRequest;

/**
 * Thread-safe queue for responses from game thread to Netty thread.
 * Gameplay services submit responses here.
 * NetworkProcessorSystem polls and sends them to clients.
 */
@Component
public class NetworkingRequestQueue {
	
	private final ConcurrentLinkedQueue<INetworkingRequest> requests = 
		new ConcurrentLinkedQueue<>();

	/**
	 * Submit a response to send to client.
	 * Thread-safe.
	 */
	public void submit(INetworkingRequest request) {
		requests.offer(request);
	}

	/**
	 * Poll a response on game thread.
	 */
	public INetworkingRequest poll() {
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