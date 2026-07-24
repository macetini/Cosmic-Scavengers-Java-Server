package com.cosmic.scavengers.db.ingestion;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.yaml.AbstractYamlIngester;
import com.cosmic.scavengers.db.ingestion.conf.BlueprintsConf;
import com.cosmic.scavengers.db.ingestion.exceptions.IngestionMappingException;
import com.cosmic.scavengers.db.jpa.domain.EntityBlueprint;
import com.cosmic.scavengers.db.jpa.repositories.EntityBlueprintJpaRepository;
import com.cosmic.scavengers.db.jpa.repositories.IngestionMetadataJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BlueprintsIngestionService extends AbstractYamlIngester {
	private static final Logger log = LoggerFactory.getLogger(BlueprintsIngestionService.class);

	private final EntityBlueprintJpaRepository blueprintJpaRepository;		
	private final ObjectMapper jsonMapper;	

	public BlueprintsIngestionService(IngestionMetadataJpaRepository metaRepo, 
			EntityBlueprintJpaRepository blueprintJpaRepository,			
			ObjectMapper mapper) {
		super(metaRepo);

		this.blueprintJpaRepository = blueprintJpaRepository;		
		this.jsonMapper = mapper;
	}

	/**
	 * Entry point called by the DataInitializer. Scans
	 * 'classpath:definitions/entity_blueprints/*.yaml' and processes changes.
	 */
	@Transactional
	public void sync() {
		this.syncDirectory(BlueprintsConf.DIRECTORY.key(), this::processBlueprintData);
	}

	private void processBlueprintData(Map<String, Map<String, Object>> fullYamlData, String category) {
		log.debug("Synchronizing {} Blueprint definitions for category: [{}]", fullYamlData.size(), category);

		fullYamlData.forEach((rawBlueprintId, fileContent) -> {
			String sanitizedId = rawBlueprintId.replace(" ", "_").toUpperCase();
			if (!rawBlueprintId.toUpperCase().equals(sanitizedId)) {
				log.warn("Blueprint ID '{}' sanitized to '{}'.", rawBlueprintId, sanitizedId);
			}
									
			Map<String, Object> newConfigs = processBehaviorConfigs(sanitizedId, fileContent);
			
			EntityBlueprint updatedBlueprint = updateBlueprint(sanitizedId, newConfigs);
			
			saveBlueprint(updatedBlueprint, category);
			
			log.trace("Synced blueprint: {}", sanitizedId);
		});
	}
	
	private Map<String, Object> processBehaviorConfigs(String sanitizedId, Map<String, Object> properties) {
		final Map<?, ?> entityBehaviorConfigs = resolveBehaviorConfigs(properties);
								
		@SuppressWarnings("unchecked")
		final Map<String, Object> entityTraitDefinitions = 
				(Map<String, Object>) entityBehaviorConfigs.get("traits");
		
		Map<String, Object> newConfigs = new LinkedHashMap<>();
		
		if (entityTraitDefinitions == null) {
		    log.warn("Blueprint [{}] has no traits defined. Skipping.", sanitizedId);
		    return newConfigs;
		}
		
		List<String> traitNames = new ArrayList<>(entityTraitDefinitions.keySet());
		newConfigs.put(BlueprintsConf.TRAIT_NAMES.key(), traitNames);
		
		Map<String, Map<?, ?>> traitOverrides = extractEntityTraitOverrides(sanitizedId, entityTraitDefinitions);
		
		if(traitOverrides.size() > 0) {
			newConfigs.put(BlueprintsConf.TRAIT_OVERRIDES.key(), traitOverrides);
		}
		
		return newConfigs;
	}

	private Map<?, ?> resolveBehaviorConfigs(Map<String, Object> properties) {
		Object behaviorConfigs = properties.get("behaviorConfigs");
		if (behaviorConfigs instanceof Map<?, ?> map) {
			return map;
		}

		Object legacyBehaviorConfigs = properties.get(BlueprintsConf.BEHAVIOR_CONFIGS.key());
		if (legacyBehaviorConfigs instanceof Map<?, ?> map) {
			return map;
		}

		return Map.of();
	}
	
	private Map<String, Map<?, ?>> extractEntityTraitOverrides(String blueprintId, Map<String, Object> entityTraitDefinitions) {
		return entityTraitDefinitions.entrySet().stream().filter(entry -> {
			if (entry.getValue() == null) {
				log.trace("Blueprint [{}] has an empty trait value for key: {}. Skipping.", blueprintId, entry.getKey());
				return false;
			}
			
			if (!(entry.getValue() instanceof Map)) {
				log.warn("Blueprint [{}] expected a Map for trait [{}], but got {}. Skipping.", blueprintId, entry.getKey(),
						entry.getValue().getClass().getSimpleName());
				return false;
			}
			return true;
		})
		.collect(
				Collectors.toMap(
						Map.Entry::getKey, entry -> new LinkedHashMap<>((Map<?, ?>) entry.getValue()),
						(oldValue, newValue) -> oldValue, LinkedHashMap::new)
				);
	}
	
	private EntityBlueprint updateBlueprint(String blueprintId, Map<String, Object> newConfigs) {
		Map<String, Object> processedProperties = new LinkedHashMap<>();
		processedProperties.put(BlueprintsConf.BEHAVIOR_CONFIGS.key(), newConfigs);

		final EntityBlueprint entityBlueprint = 
				blueprintJpaRepository.findById(blueprintId).orElseGet(() -> createNewBlueprint(blueprintId));
		
		try {				
			jsonMapper.updateValue(entityBlueprint, processedProperties);
		}
		catch (JsonProcessingException e) {
			log.error("Failed to parse JSON for blueprint [{}]: {}", blueprintId, e.getMessage());
			throw new IngestionMappingException("Failed to parse JSON of Blueprint Id: " + blueprintId, e);
		}		
		catch (Exception e) {
			log.error("Failed to map blueprint [{}]: {}", blueprintId, e.getMessage());
			throw new IngestionMappingException("Failed to map Blurprint Id: " + blueprintId, e);
		}
		
		return entityBlueprint;
	}
	
	private EntityBlueprint createNewBlueprint(String blueprintId) {
		EntityBlueprint newBlueprint = new EntityBlueprint();
		newBlueprint.setId(blueprintId);
		newBlueprint.setCreatedAt(OffsetDateTime.now());
		newBlueprint.setVersion(0);		
		
		return newBlueprint;
	}
	
	private void saveBlueprint(EntityBlueprint blueprint, String category) {
		blueprint.setCategoryId(category.toUpperCase());
		blueprint.setUpdatedAt(OffsetDateTime.now());

		blueprintJpaRepository.saveAndFlush(blueprint);
		log.trace("Saved blueprint: {}", blueprint.getId());
	}
}
