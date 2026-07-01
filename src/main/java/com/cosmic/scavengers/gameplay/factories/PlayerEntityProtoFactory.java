package com.cosmic.scavengers.gameplay.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.utils.DecimalUtil;
import com.cosmic.scavengers.core.utils.ProtobufTimeUtil;
import com.cosmic.scavengers.db.jpa.model.BlueprintTemplate;
import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.gameplay.registries.BlueprintRegistry;
import com.cosmic.scavengers.gameplay.registries.TraitRegistry;
import com.cosmic.scavengers.networking.mappers.TraitProtobufMapper;
import com.cosmicscavengers.networking.protobuf.entities.PlayerEntityProto;

/**
 * Factory for building PlayerEntityProto from database entities.
 * Handles all proto conversion logic.
 */
@Component
public class PlayerEntityProtoFactory {
	private static final Logger log = LoggerFactory.getLogger(PlayerEntityProtoFactory.class);

	private final BlueprintRegistry blueprintRegistry;
	private final TraitRegistry traitRegistry;
	private final TraitProtobufMapper traitProtobufMapper;

	public PlayerEntityProtoFactory(
			BlueprintRegistry blueprintRegistry,
			TraitRegistry traitRegistry,
			TraitProtobufMapper traitProtobufMapper) {
		this.blueprintRegistry = blueprintRegistry;
		this.traitRegistry = traitRegistry;
		this.traitProtobufMapper = traitProtobufMapper;
	}

	/**
	 * Build a PlayerEntityProto from a database entity.
	 */
	public PlayerEntityProto build(PlayerEntities entity) {
		Long playerId = entity.getPlayerId();
		Long entityId = entity.getId();
		String blueprintId = normalizeBlueprintId(entity.getBlueprintId());

		log.debug("Building Proto for PlayerId: '{}' | EntityId: '{}' | Blueprint: '{}'", 
			playerId, entityId, blueprintId);

		PlayerEntityProto.Builder builder = PlayerEntityProto.newBuilder();
		applyBaseFields(builder, entity, playerId, blueprintId);

		blueprintRegistry.get(blueprintId).ifPresentOrElse(
				blueprint -> addTraitsToBuilder(builder, blueprint, playerId, entityId),
				() -> log.warn("Blueprint not found: '{}' for PlayerId '{}' EntityId '{}'", 
					blueprintId, playerId, entityId));

		return builder.build();
	}

	private void applyBaseFields(
			PlayerEntityProto.Builder builder, 
			PlayerEntities entity,
			Long playerId, String blueprintId) {
		builder
				.setId(entity.getId())
				.setPlayerId(playerId)
				.setWorldId(entity.getWorldId())
				.setSectorId(entity.getSectorId())
				.setBlueprintId(blueprintId)
				.setStatusId(entity.getStatusId())
				.setEntityName(normalizeEntityName(entity.getEntityName()))
				.setIsStatic(entity.getIsStatic())
				.setPosX(DecimalUtil.toScaled(entity.getPosX()))
				.setPosY(DecimalUtil.toScaled(entity.getPosY()))
				.setPosZ(DecimalUtil.toScaled(entity.getPosZ()))
				.setRotation(entity.getRotation())
				.setChunkX(entity.getChunkX())
				.setChunkY(entity.getChunkY())
				.setCurrentHealth(entity.getCurrentHealth())
				.setCreatedAt(ProtobufTimeUtil.toProtobufTimestamp(entity.getCreatedAt()))
				.setUpdatedAt(ProtobufTimeUtil.toProtobufTimestamp(entity.getUpdatedAt()));
	}

	private void addTraitsToBuilder(
			PlayerEntityProto.Builder builder,
			BlueprintTemplate blueprint,
			Long playerId, 
			Long entityId) {
		blueprint.traitIds().forEach(traitId -> 
			traitRegistry.get(traitId).ifPresentOrElse(
				trait -> {
					if (log.isTraceEnabled()) {
						log.trace("Processing Trait [{}] for PlayerId '{}' EntityId '{}' - Properties: [{}]", 
							traitId, playerId, entityId, trait);
					}
					traitProtobufMapper.mapToProto(traitId, trait)
						.ifPresent(builder::addTraits);
				},
				() -> log.warn("Trait not found: {} for PlayerId '{}' EntityId '{}'", 
					traitId, playerId, entityId)));
	}

	private String normalizeBlueprintId(String blueprintId) {
		return blueprintId != null ? blueprintId : "";
	}

	private String normalizeEntityName(String entityName) {
		return entityName != null ? entityName : "";
	}
}