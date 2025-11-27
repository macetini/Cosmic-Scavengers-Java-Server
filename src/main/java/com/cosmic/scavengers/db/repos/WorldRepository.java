package com.cosmic.scavengers.db.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.World;

@Repository
public interface WorldRepository extends JpaRepository<World, Long> {

	/**
	 * Finds a World entity by its unique name.
	 */
	Optional<World> findByWorldName(String worldName);

	/**
	 * Finds the default world entry (assuming ID 1) which is critical for
	 * initialization.
	 */
	default Optional<World> findDefaultWorld() {
		return findById(1L);
	}
}