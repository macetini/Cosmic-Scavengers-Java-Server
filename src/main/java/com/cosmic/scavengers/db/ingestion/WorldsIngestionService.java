package com.cosmic.scavengers.db.ingestion;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.yaml.AbstractYamlIngester;
import com.cosmic.scavengers.db.ingestion.conf.WorldsConf;
import com.cosmic.scavengers.db.ingestion.exceptions.IngestionMappingException;
import com.cosmic.scavengers.db.jpa.domain.World;
import com.cosmic.scavengers.db.jpa.repositories.IngestionMetadataJpaRepository;
import com.cosmic.scavengers.db.jpa.repositories.WorldJpaRepository;

@Service
public class WorldsIngestionService extends AbstractYamlIngester {
	private static final Logger log = LoggerFactory.getLogger(WorldsIngestionService.class);
	
	private final Set<Long> processedIds = new HashSet<>();
	
	private final WorldJpaRepository worldsJpaRepository;	

	public WorldsIngestionService(IngestionMetadataJpaRepository metaRepository,
			WorldJpaRepository worldsJpaRepository) {
		super(metaRepository);
		this.worldsJpaRepository = worldsJpaRepository;
	}

	/**
	 * Entry point called by the DataInitializer. Scans 'classpath:worlds/*.yaml'
	 * and processes changes.
	 */
	@Transactional
	public void sync() {
		this.syncDirectory(WorldsConf.DIRECTORY.key(), this::processWorldsData);
	}

	/**
	 * The implementation of the BiConsumer expected by syncDirectory. Maps the raw
	 * YAML data to our JPA Entity.
	 */
	private void processWorldsData(Map<String, Map<String, Object>> data, String category) {		
		log.debug("Synchronizing {} Worlds definitions for category: [{}]", data.size(), category);

		// category is the filename without extension (e.g. "1_alpha")
		String[] parts = category.split("_", 2);
		
		if (parts.length != 2) {
			log.error("World definition filename must follow the pattern 'ID_Name.yaml'. Found: {}.yaml", category);
			return;
		}

		Long worldId;
		try {
			worldId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			log.error("Invalid World ID in filename '{}.yaml': {}", category, parts[0]);
			return;
		}
		
		if (!processedIds.add(worldId)) {
			log.error(
					"CRITICAL: Duplicate World ID detected! File '{}.yaml' is trying to use ID '{}', which was already claimed by another file. Skipping.",
					category, worldId);
			throw new IngestionMappingException("Duplicate World ID detected: " + worldId);
		}
		
		String worldName = parts[1];

		data.forEach((yamlKeyName, properties) -> {
			if (!yamlKeyName.equals(worldName)) {
				log.warn("YAML root key '{}' does not match filename name '{}'. Using filename.", yamlKeyName, worldName);
			}

			World world = worldsJpaRepository.findById(worldId).orElseGet(() -> {
				World newWorld = new World();
				newWorld.setId(worldId);
				newWorld.setCreatedAt(OffsetDateTime.now());
				return newWorld;
			});

			world.setName(worldName);
			world.setConfig(properties);
			world.setUpdatedAt(OffsetDateTime.now());

			worldsJpaRepository.save(world);
			
			log.trace("Synced world: {} with ID: {}", worldName, worldId);
		});
	}
}
