package com.cosmic.scavengers.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Placeholder for the Spring Data JPA Repository for Player entities (inferred
 * from logs). NOTE: The actual Player entity class is required for this
 * interface to be fully functional.
 */
@Repository
public interface UserRepository extends JpaRepository<Player, Long> {
	// Methods like findByUsername(String username) would go here
}