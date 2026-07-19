package com.cosmic.scavengers.db.ingestion;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	
	private record WorldMetadata(Long id, String name) {
	}

	private final WorldJpaRepository worldsJpaRepository;

	public WorldsIngestionService(IngestionMetadataJpaRepository metaRepository, WorldJpaRepository worldsJpaRepository) {
		super(metaRepository);
		this.worldsJpaRepository = worldsJpaRepository;
	}

	/**
	 * Entry point called by the DataInitializer.
	 */
	@Transactional
	public void sync() {
		// Moving tracking inside the execution scope fixes the Singleton reuse bug
		Set<Long> processedIds = new HashSet<>();
		List<World> entitiesToSave = new ArrayList<>();

		// Adjusting the internal call so we can collect entities across file iterations
		this.syncDirectory(WorldsConf.DIRECTORY.key(), (data, category) -> {
			processWorldsData(data, category, processedIds, entitiesToSave);
		});

		if (!entitiesToSave.isEmpty()) {
			worldsJpaRepository.saveAll(entitiesToSave);
			log.info("Successfully batched and saved {} world definitions.", entitiesToSave.size());
		}
	}

	private void processWorldsData(
			Map<String, Map<String, Object>> data, 
			String category, 
			Set<Long> processedIds,
			List<World> entitiesToSave) {
		log.debug("Synchronizing {} Worlds definitions for category: [{}]", data.size(), category);

		WorldMetadata metadata = parseAndValidateMetadata(category, processedIds);
		if (metadata == null) {
			log.warn("Skipping file '{}.yaml' due to parsing errors.", category);
			return;
		}

		if (data.size() > 1) {
			log.warn("File '{}.yaml' contains multiple root keys. Only the matching key will be prioritized safely.", category);
		}

		data.forEach((yamlKeyName, properties) -> {
			mapYamlToEntity(yamlKeyName, properties, metadata, entitiesToSave);
		});
	}

	/**
	 * Extracts and validates the world ID and name from the filename.
	 */
	private WorldMetadata parseAndValidateMetadata(String category, Set<Long> processedIds) {
		String[] parts = category.split("_", 2);
		if (parts.length != 2) {
			log.error("World definition filename must follow the pattern 'ID_Name.yaml'. Found: {}.yaml", category);
			return null;
		}

		Long worldId;
		try {
			worldId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			log.error("Invalid World ID in filename '{}.yaml': {}", category, parts[0]);
			return null;
		}

		if (!processedIds.add(worldId)) {
			log.error("CRITICAL: Duplicate World ID detected! File '{}.yaml' targets ID '{}' which was already processed.", category,
					worldId);
			throw new IngestionMappingException("Duplicate World ID detected: " + worldId);
		}

		return new WorldMetadata(worldId, parts[1]);
	}

	/**
	 * Maps a single YAML root block to a World entity and stages it for batch
	 * saving.
	 */
	private void mapYamlToEntity(String yamlKeyName, 
			Map<String, Object> properties, 
			WorldMetadata metadata, 
			List<World> entitiesToSave) {
		if (!yamlKeyName.equals(metadata.name())) {
			log.warn("YAML root key '{}' does not match filename name '{}'. Skipping key to avoid corruption.", yamlKeyName,
					metadata.name());
			return;
		}

		World world = worldsJpaRepository.findById(metadata.id()).orElseGet(() -> {
			World newWorld = new World();
			newWorld.setId(metadata.id());
			newWorld.setCreatedAt(OffsetDateTime.now());
			return newWorld;
		});

		world.setName(metadata.name());
		world.setConfig(properties);
		world.setUpdatedAt(OffsetDateTime.now());

		entitiesToSave.add(world);
		log.trace("Staged world for save: {} with ID: {}", metadata.name(), metadata.id());
	}
}
