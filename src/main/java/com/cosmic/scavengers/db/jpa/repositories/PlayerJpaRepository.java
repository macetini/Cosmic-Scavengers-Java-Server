package com.cosmic.scavengers.db.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cosmic.scavengers.db.jpa.domain.Player;

@Repository
public interface PlayerJpaRepository extends JpaRepository<Player, Long> {

	Optional<Player> findByUsername(String username);

	boolean existsByUsername(String username);
}
