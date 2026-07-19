package com.cosmic.scavengers.db.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class DatabaseExecutor {
	// This executor instantly spawns a new Virtual Thread for every task!
	private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

	public void execute(Runnable task) {
		executor.submit(task);
	}
}