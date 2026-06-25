package com.cosmic.scavengers.registries;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.db.jpa.model.BlueprintTemplate;
import com.cosmic.scavengers.db.services.BlueprintService;

@Component
public class BlueprintRegistry {
	private static final Logger log = LoggerFactory.getLogger(BlueprintRegistry.class);

	private final BlueprintService blueprintService;
	private final TraitRegistry traitRegistry;
	
	private final Map<String, BlueprintTemplate> cache = new ConcurrentHashMap<>();	

	public BlueprintRegistry(BlueprintService blueprintService,
			TraitRegistry traitRegistry) {
		this.blueprintService = blueprintService;
		this.traitRegistry = traitRegistry;
	}

	public void load() {
		log.debug("Caching Entity Blueprints from DB.");

		cache.clear();

		blueprintService.loadAllTemplates().forEach(blueprintTemplate -> {
			log.trace("Chaching BlueprintId '{}'", blueprintTemplate.id());
						
			blueprintTemplate.traitIds().forEach(traitId -> {	
				log.trace("Processing TraitId '{}' for BlueprintId '{}'", traitId, blueprintTemplate.id());
				
			    Map<String, Object> finalProperties = new HashMap<>();
			    Optional<Map<String, Object>> traitDefenitions = traitRegistry.get(traitId);
			    
			    if(traitDefenitions.isPresent()) {
			        finalProperties.putAll(traitDefenitions.get());
			    } else {
			        log.warn("Blueprint '{}' requires missing Trait: {}", blueprintTemplate.id(), traitId);
			        return;
			    }
			    			    
			    Map<String, Object> yamlOverrides = blueprintTemplate.traitOverrides().get(traitId);
			    if (yamlOverrides != null) {
			        finalProperties.putAll(yamlOverrides);
			    }			    
			    
			    blueprintTemplate.traitValues().put(traitId, Collections.unmodifiableMap(finalProperties));
			});		
						
			cache.put(blueprintTemplate.id(), blueprintTemplate);
		});
		
		log.debug("Successfully cached {} Blueprints.", cache.size());
	}

	public Optional<BlueprintTemplate> get(String id) {
		return Optional.ofNullable(cache.get(id));
	}

	public Collection<BlueprintTemplate> getAll() {
		return cache.values();
	}
	
	public int getCount() {
		return cache.size();
	}
}