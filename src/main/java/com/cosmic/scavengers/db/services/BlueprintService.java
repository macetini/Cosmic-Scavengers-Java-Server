package com.cosmic.scavengers.db.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.ingestion.conf.BlueprintsConf;
import com.cosmic.scavengers.db.jpa.domain.EntityBlueprint;
import com.cosmic.scavengers.db.jpa.model.BlueprintTemplate;
import com.cosmic.scavengers.db.jpa.repositories.EntityBlueprintJpaRepository;

@Service
public class BlueprintService {

	private final EntityBlueprintJpaRepository blueprintRepository;

	public BlueprintService(EntityBlueprintJpaRepository repository) {
		this.blueprintRepository = repository;
	}

	@Transactional(readOnly = true)
	public List<BlueprintTemplate> loadAllTemplates() {
		return blueprintRepository
				.findAll()
				.stream()
				.map(this::mapToTemplate)
				.toList();
	}

	private BlueprintTemplate mapToTemplate(EntityBlueprint entity) {
		Map<String, Object> configs = entity.getBehaviorConfigs();
		
		List<String> traitIds = extractList(configs, BlueprintsConf.TRAIT_NAMES.key());
		
		Map<String, Map<String, Object>> traitOverrides = 
				extractValues(configs, BlueprintsConf.TRAIT_OVERRIDES.key());
		
		List<String> buffIds = extractList(configs, "buffs");

		return new BlueprintTemplate(
				entity.getId(), 
				entity.getCategoryId(), 
				entity.getBaseHealth(), 
				entity.isStaticDefault(), 
				traitIds,
				traitOverrides, 
				Map.of(), 
				buffIds);
	}

	private List<String> extractList(Map<String, Object> map, String key) {
		if (map == null) {
			return List.of();
		}
		
		Object value = map.get(key);
		
		return (value instanceof List<?> list) ? 
				(List<String>) list : List.of();
	}

	private Map<String, Map<String, Object>> extractValues(Map<String, Object> map, String key) {
		if (map == null) {
			return Map.of();
		}
		
		Object value = map.get(key);
		
		return (value instanceof Map<?, ?> overrides) ? 
				(Map<String, Map<String, Object>>) overrides : Map.of();
	}
}
