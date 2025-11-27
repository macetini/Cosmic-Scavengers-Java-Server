package com.cosmic.scavengers.db.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.PlayerAttribute;

@Repository
public interface PlayerAttributeRepository extends JpaRepository<PlayerAttribute, Long> {

	/**
	 * Finds the attributes record that points to a specific main base ID.
	 */
	PlayerAttribute findByMainBaseEntityId(Long mainBaseEntityId);
}