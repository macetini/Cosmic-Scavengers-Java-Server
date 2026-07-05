package com.cosmic.scavengers.db.ingestion;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.core.yaml.AbstractYamlIngester;
import com.cosmic.scavengers.db.ingestion.conf.TraitsConf;
import com.cosmic.scavengers.db.jpa.domain.TraitDefinition;
import com.cosmic.scavengers.db.jpa.repositories.IngestionMetadataJpaRepository;
import com.cosmic.scavengers.db.jpa.repositories.TraitDefinitionJpaRepository;

@Service
public class TraitsIngestionService extends AbstractYamlIngester {
	private static final Logger log = LoggerFactory.getLogger(TraitsIngestionService.class);	

	private final TraitDefinitionJpaRepository traitJpaRepository;	

	public TraitsIngestionService(IngestionMetadataJpaRepository metaRepository, 
			TraitDefinitionJpaRepository traitJpaRepository) {
		super(metaRepository);
		
		this.traitJpaRepository = traitJpaRepository;		
	}

	/**
	 * Entry point called by the DataInitializer. Scans 'classpath:traits/*.yaml'
	 * and processes changes.
	 */
	@Transactional
	public void sync() {
		this.syncDirectory(TraitsConf.DIRECTORY.key(), this::processTraitData);
	}

	/**
	 * The implementation of the BiConsumer expected by syncDirectory. Maps the raw
	 * YAML data to our JPA Entity.
	 */
	private void processTraitData(Map<String, Map<String, Object>> data, String category) {		
		log.debug("Synchronizing {} Trait definitions for category: [{}]", data.size(), category);

		data.forEach((traitId, properties) -> {		
			final TraitDefinition trait = traitJpaRepository.findById(traitId).orElse(new TraitDefinition());

			trait.setId(traitId);
			trait.setCategory(category.toUpperCase());
			trait.setData(properties);

			traitJpaRepository.save(trait);
			
			log.trace("Synced trait: {}", traitId);
		});
	}
}
