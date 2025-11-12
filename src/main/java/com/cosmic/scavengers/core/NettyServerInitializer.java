package com.cosmic.scavengers.core;

import com.cosmic.scavengers.db.UserRepository;
import com.cosmic.scavengers.engine.GameEngine;
import com.cosmic.scavengers.networking.GameChannelHandler;
import com.cosmic.scavengers.networking.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// 🎯 Implements CommandLineRunner (to execute on startup) 
// 🎯 Implements IMessageBroadcaster (to break the circular dependency with GameEngine)
@Component
public class NettyServerInitializer implements CommandLineRunner, IMessageBroadcaster {
	private static final Logger log = LoggerFactory.getLogger(NettyServerInitializer.class);

	// --- Dependencies Injected by Spring ---
	private final GameEngine engine;
	private final UserRepository userRepository;

	// --- Server State ---
	// NOTE: This field must remain synchronized as it's modified by multiple Netty
	// threads.
	private final Set<GameChannelHandler> channelHandlers = Collections.synchronizedSet(new HashSet<>());

	// Constructor Injection: Spring provides the GameEngine and UserRepository
	// instances.
	public NettyServerInitializer(GameEngine engine, UserRepository userRepository) {
		this.engine = engine;
		this.userRepository = userRepository;
	}

	// Executes when the Spring application context is loaded.
	@Override
	public void run(String... args) throws Exception {
		log.info("Cosmic Scavengers Server initialization starting...");

		// 1. Start GameEngine in a separate thread
		new Thread(engine).start();

		// 2. Start Netty Server (It needs the engine, repository, and a reference to
		// THIS component)
		new NettyServer(engine, userRepository, this).run();

		log.info("Cosmic Scavengers Server initialization complete.");
	}

	// --- IMessageBroadcaster Interface Implementation ---

	@Override
	public void broadcast(String message, GameChannelHandler sender) {
		for (GameChannelHandler handler : channelHandlers) {
			// Only send to others, not the source channel
			if (handler != sender) {
				handler.sendMessage(message);
			}
		}
	}

	// --- Connection Management Methods ---

	public void addChannelHandler(GameChannelHandler handler) {
		channelHandlers.add(handler);
		log.info("Netty client connected. Active channels: {}", channelHandlers.size());
	}

	public void removeChannelHandler(GameChannelHandler handler) {
		channelHandlers.remove(handler);
		log.info("Netty client disconnected. Active channels: {}", channelHandlers.size());
	}
}