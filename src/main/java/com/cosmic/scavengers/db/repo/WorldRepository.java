package com.cosmic.scavengers.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.World;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {

	/**
	 * Finds a World entity by its unique name.
	 */
	World findByWorldName(String worldName);
}