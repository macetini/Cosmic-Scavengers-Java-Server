package com.cosmic.scavengers.services.jooq;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;
import com.cosmic.scavengers.db.repository.jooq.JooqPlayerEntitiyRepository;
import com.cosmic.scavengers.db.repository.jooq.JooqWorldRepository;

@Service
public class PlayerInitService {
	private JooqWorldRepository jooqWorlRepository;
	private JooqPlayerEntitiyRepository jooqPlayerEntitiyRepository;

	public PlayerInitService(JooqWorldRepository jooqWorlRepository,
			JooqPlayerEntitiyRepository jooqPlayerEntitiyRepository) {
		this.jooqWorlRepository = jooqWorlRepository;
		this.jooqPlayerEntitiyRepository = jooqPlayerEntitiyRepository;
	}

	public Optional<Worlds> getCurrentWorldDataByPlayerId(long playerId) {
		return jooqWorlRepository.getById(1);
	}

	public List<PlayerEntities> getAllByPlayerId(long playerId) {
		return jooqPlayerEntitiyRepository.getAllByPlayerId(playerId);
	}

}
