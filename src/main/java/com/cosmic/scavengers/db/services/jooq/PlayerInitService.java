package com.cosmic.scavengers.db.services.jooq;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;
import com.cosmic.scavengers.db.repository.jooq.JooqPlayerEntitiyRepository;
import com.cosmic.scavengers.db.repository.jooq.JooqWorldRepository;

@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private JooqWorldRepository jooqWorldRepository;
	private JooqPlayerEntitiyRepository jooqPlayerEntityRepository;

	public PlayerInitService(JooqWorldRepository jooqWorlRepository,
			JooqPlayerEntitiyRepository jooqPlayerEntitiyRepository) {
		this.jooqWorldRepository = jooqWorlRepository;
		this.jooqPlayerEntityRepository = jooqPlayerEntitiyRepository;

		log.info("PlayerInitService initialized with Jooq repositories.");
	}

	public Worlds getCurrentWorldDataByPlayerId(long playerId) {
		Optional<Worlds> worldOptional = jooqWorldRepository.getById(playerId);
		return worldOptional
				.orElseThrow(() -> new IllegalStateException("No world data found for player with ID: " + playerId));
	}

	public List<PlayerEntities> getAllByPlayerId(long playerId) {
		return jooqPlayerEntityRepository.getAllByPlayerId(playerId);
	}

}
