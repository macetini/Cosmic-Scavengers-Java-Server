package com.cosmic.scavengers.db.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.jpa.domain.World;
import com.cosmic.scavengers.db.jpa.repositories.WorldJpaRepository;

@Service
public class WorldService {
	private static final Logger log = LoggerFactory.getLogger(WorldService.class);

	private final WorldJpaRepository worldRepository;

	public WorldService(WorldJpaRepository worldRepository) {
		this.worldRepository = worldRepository;
	}

	public List<World> loadAllWorlds() {
		return worldRepository.findAll();
	}	
}
