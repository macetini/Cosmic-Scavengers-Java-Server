package com.cosmic.scavengers.db.services.jooq;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;
import com.cosmic.scavengers.db.repositories.jooq.JooqPlayerEntitiyRepository;
import com.cosmic.scavengers.db.repositories.jooq.JooqWorldRepository;
import com.cosmic.scavengers.dominion.components.Owner;
import com.cosmic.scavengers.dominion.components.Position;
import com.cosmic.scavengers.dominion.tags.StaticTag;
import com.cosmic.scavengers.gameplay.registry.EntityRegistry;
import com.cosmic.scavengers.utils.DecimalUtils;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;

@Service
public class PlayerInitService {
	private static final Logger log = LoggerFactory.getLogger(PlayerInitService.class);

	private final JooqWorldRepository jooqWorldRepository;
	private final JooqPlayerEntitiyRepository jooqPlayerEntityRepository;
	private final EntityRegistry entityRegistry;
	private final Dominion dominion;

	public PlayerInitService(JooqWorldRepository jooqWorldRepository,
			JooqPlayerEntitiyRepository jooqPlayerEntityRepository, EntityRegistry entityRegistry, Dominion dominion) {
		this.jooqWorldRepository = jooqWorldRepository;
		this.jooqPlayerEntityRepository = jooqPlayerEntityRepository;
		this.entityRegistry = entityRegistry;
		this.dominion = dominion;
	}

	public Worlds getCurrentWorldDataByPlayerId(long playerId) {
		log.info("Fetching world data for player {}", playerId);

		final Optional<Worlds> worldOptional = jooqWorldRepository.getById(playerId);
		return worldOptional
				.orElseThrow(() -> new IllegalStateException(
						"No world data found for player with ID: " + playerId));
	}

	/**
	 * Fetches entities from DB and ensures they exist in the live ECS simulation.
	 */
	public List<PlayerEntities> fetchAndInitializeEntities(long playerId) {
		log.info("Fetching entities for player {}", playerId);
		final List<PlayerEntities> entities = jooqPlayerEntityRepository.getAllByPlayerId(playerId);

		log.debug("Fetched {} entities for player {}", entities.size(), playerId);
		for (PlayerEntities entity : entities) {
			if (!entityRegistry.isActive(entity.getId())) {
				spawnInEcs(entity);
			}
		}
		return entities;
	}

	private void spawnInEcs(PlayerEntities entity) {
		log.debug("Spawning Entity {} into live simulation for Player {}", entity.getId(), entity.getPlayerId());
		
		final Position initialPos = new Position(
				DecimalUtils.fromFloat(entity.getPosX()),
				DecimalUtils.fromFloat(entity.getPosY()), 
				DecimalUtils.fromFloat(entity.getPosZ()));

		final Entity liveEntity = dominion.createEntity(initialPos, new Owner(entity.getPlayerId()));		

		if (Boolean.TRUE.equals(entity.getIsStatic())) {
			liveEntity.add(new StaticTag());
		}

		entityRegistry.register(entity.getId(), liveEntity);
	}
}
