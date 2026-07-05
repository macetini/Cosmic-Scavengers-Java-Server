package com.cosmic.scavengers.db.ingestion;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.yaml.AbstractYamlIngester;
import com.cosmic.scavengers.db.ingestion.conf.WorldsConf;
import com.cosmic.scavengers.db.jpa.repositories.IngestionMetadataJpaRepository;
import com.cosmic.scavengers.db.jpa.repositories.WorldJpaRepository;

@Service
public class WorldsIngestionService extends AbstractYamlIngester {
	private static final Logger log = LoggerFactory.getLogger(WorldsIngestionService.class);		
	
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
		this.syncDirectory(WorldsConf.DIRECTORY.key(), this::processTraitData);
	}

	/**
	 * The implementation of the BiConsumer expected by syncDirectory. Maps the raw
	 * YAML data to our JPA Entity.
	 */
	private void processTraitData(Map<String, Map<String, Object>> data, String category) {		
		log.debug("Synchronizing {} Worlds definitions for category: [{}]", data.size(), category);

//		data.forEach((traitId, properties) -> {		
//			final TraitDefinition trait = traitRepository.findById(traitId).orElse(new TraitDefinition());
//
//			trait.setId(traitId);
//			trait.setCategory(category.toUpperCase());
//			trait.setData(properties);
//
//			traitRepository.save(trait);
//			
//			log.trace("Synced trait: {}", traitId);
//		});
	}
}
