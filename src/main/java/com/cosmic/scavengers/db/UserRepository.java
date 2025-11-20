package com.cosmic.scavengers.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.Player;

/**
 * Spring Data JPA Repository for Player entities. It manages the Player entity,
 * using Long as the primary key type.
 */
@Repository
public interface UserRepository extends JpaRepository<Player, Long> {

	/**
	 * Finds a Player by their unique username. Spring Data automatically implements
	 * this based on the method name. * @param username The username to search for.
	 * 
	 * @return The Player entity if found, or null otherwise (assuming
	 *         JpaRepository's finders can return null).
	 */
	Player findByUsername(String username);
}