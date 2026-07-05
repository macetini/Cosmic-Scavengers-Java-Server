package com.cosmic.scavengers.db.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.jpa.domain.WorldSector;

@Repository
public interface WorldSectorJpaRepository extends JpaRepository<WorldSector, Long> {

	Optional<WorldSector> findByWorld_IdAndChunkXAndChunkY(Long worldId, Integer chunkX, Integer chunkY);

	List<WorldSector> findByWorld_Id(Long worldId);
}
