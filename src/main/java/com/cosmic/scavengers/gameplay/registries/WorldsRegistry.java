package com.cosmic.scavengers.gameplay.registries;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.jpa.domain.World;
import com.cosmic.scavengers.db.services.WorldService;

@Component
public class WorldsRegistry {
	private static final Logger log = LoggerFactory.getLogger(WorldsRegistry.class);

	private final WorldService worldService;
	
	// Fast lookup for the Game Engine
	private final Map<Long, World> cache = new ConcurrentHashMap<>();

	public WorldsRegistry(WorldService worldService) {
		this.worldService = worldService;
	}

	public void load() {
		log.debug("Caching Worlds from DB.");

		cache.clear();		

		worldService.loadAllWorlds().forEach(world -> {
			log.trace("Caching World '{}' [ID: {}]", world.getName(), world.getId());
			cache.put(world.getId(), world);			
		});
		
		log.debug("Successfully cached {} Worlds.", cache.size());
	}

	public Optional<World> get(Long id) {
		return Optional.ofNullable(cache.get(id));
	}

	public Collection<World> getAll() {
		return cache.values();
	}
	
	public int getCount() {
		return cache.size();
	}
}
