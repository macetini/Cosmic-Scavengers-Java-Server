package com.cosmic.scavengers.db.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.jpa.domain.World;

@Repository
public interface WorldJpaRepository extends JpaRepository<World, Long> {

	Optional<World> findByWorldName(String worldName);
}
