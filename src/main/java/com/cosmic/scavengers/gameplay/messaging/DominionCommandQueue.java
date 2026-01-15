package com.cosmic.scavengers.gameplay.messaging;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

@Component
public class DominionCommandQueue {
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