package com.cosmic.scavengers.db.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.WorldEntity;

@Repository
public interface WorldEntityRepository extends JpaRepository<WorldEntity, Long> {

	/**
	 * Finds all World Entities within a specific chunk for a given World.
	 */
	List<WorldEntity> findByWorldIdAndChunkXAndChunkY(Long worldId, Integer chunkX, Integer chunkY);

	/**
	 * Finds all Bases owned by a specific Player.
	 */
	List<WorldEntity> findByOwnerPlayerIdAndEntityType(Long playerId, String entityType);
}