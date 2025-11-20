package com.cosmic.scavengers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cosmic.scavengers.core.IMessageBroadcaster;
import com.cosmic.scavengers.networking.MessageBroadcasterImpl;
import com.cosmic.scavengers.system.MovementSystem;

import dev.dominion.ecs.api.Dominion;

@Configuration
public class Config {
	@Bean
	public Dominion dominion() {
		return Dominion.create();
	}

	@Bean
	public MovementSystem movementSystem(Dominion dominion) {
		return new MovementSystem(dominion);
	}

	@Bean
	public IMessageBroadcaster messageBroadcaster() {
		return new MessageBroadcasterImpl();
	}
}