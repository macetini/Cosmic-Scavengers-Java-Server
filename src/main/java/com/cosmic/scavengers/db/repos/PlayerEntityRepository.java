package com.cosmic.scavengers.db.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.PlayerEntity;

/**
 * Spring Data JPA Repository for database operations on the PlayerEntity.
 */
@Repository
public interface PlayerEntityRepository extends JpaRepository<PlayerEntity, Long> {

	/**
	 * Finds all game entities owned by a specific player ID. Spring Data
	 * automatically implements this query based on the method name.
	 */
	List<PlayerEntity> findAllByPlayerId(Long playerId);
}