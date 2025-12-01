package com.cosmic.scavengers.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.meta.World;
import com.cosmic.scavengers.db.repos.WorldRepository;
import com.cosmic.scavengers.networking.meta.WorldData;

/**
 * Service dedicated to handling world-level business logic, such as retrieving
 * the default world, managing chunk generation, or world state updates.
 */
@Service
public class WorldService {
	private static final Logger log = LoggerFactory.getLogger(WorldService.class);

	private final WorldRepository worldRepository;

	public WorldService(WorldRepository worldRepository) {
		this.worldRepository = worldRepository;
	}

	/**
	 * Retrieves the default starting world from the database.
	 * 
	 * @return The default World entity.
	 * @throws IllegalStateException if the default world (ID 1) is missing (which
	 *                               should not happen if 'data.sql' runs
	 *                               correctly).
	 */
	@Transactional(readOnly = true)
	public World getDefaultWorld() {
		Optional<World> worldOptional = worldRepository.findDefaultWorld();

		if (worldOptional.isEmpty()) {
			// This case should only occur if the data.sql failed or was not executed.
			throw new IllegalStateException(
					"FATAL: Default World (ID 1) not found in the database. Initialization failed.");
		}

		return worldOptional.get();
	}

	/**
	 * Converts the persistent World Entity into the network WorldData DTO. This
	 * method is the DTO Population step.
	 *
	 * @param world         The JPA Entity retrieved from the database (Primary Data
	 *                      Source).
	 * @param currentChunkX The current X coordinate (Dynamic Data Source).
	 * @param currentChunkY The current Y coordinate (Dynamic Data Source).
	 * 
	 * @return A clean, immutable WorldData DTO ready for serialization.
	 * 
	 */
	@Transactional(readOnly = true)	
	public static Optional<WorldData> toWorldData(World world) {
		if (world == null) {
			log.warn("Attempted to convert null World entity to WorldData DTO.");
			return Optional.empty();
		}		

		WorldData data = new WorldData(world.getId(), world.getWorldName(), world.getMapSeed(),
				world.getSectorSizeUnits());

		return Optional.of(data);
	}

	/**
	 * Example method for future world generation logic. This would involve creating
	 * WorldEntity records for resources, ruins, etc., based on the world's mapSeed.
	 */
	public void generateChunkContent(World world, int chunkX, int chunkY) {
		log.debug("Generating content for chunk ({}, {}) in World ID: {}", chunkX, chunkY, world.getId());
		// Implementation for procedural generation goes here...
	}
}