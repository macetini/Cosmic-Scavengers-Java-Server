package com.cosmic.scavengers.db.repos;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.meta.PlayerUnit;

@Repository
public interface PlayerUnitRepository extends JpaRepository<PlayerUnit, Long> {

	/**
	 * Finds all PlayerUnits that are currently in transit and scheduled to arrive
	 * by a specific time. CRITICAL for the server's combat scheduler.
	 */
	List<PlayerUnit> findByIsInTransitTrueAndArrivalTimeBefore(Instant time);

	/**
	 * Finds all PlayerUnits garrisoned at a specific base.
	 */
	List<PlayerUnit> findByCurrentBaseId(Long baseId);
}