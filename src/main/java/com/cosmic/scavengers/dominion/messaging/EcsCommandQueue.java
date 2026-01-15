package com.cosmic.scavengers.dominion.messaging;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

@Component
public class EcsCommandQueue {
	private final ConcurrentLinkedQueue<EcsCommand> queue = new ConcurrentLinkedQueue<>();

	public void submit(EcsCommand request) {
		queue.add(request);
	}

	public EcsCommand poll() {
		return queue.poll();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}
}