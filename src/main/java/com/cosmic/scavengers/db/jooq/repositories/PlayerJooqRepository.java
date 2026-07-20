package com.cosmic.scavengers.db.jooq.repositories;

import static com.cosmic.scavengers.db.model.tables.Players.PLAYERS;

import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cosmic.scavengers.db.model.tables.pojos.Players;
import com.cosmic.scavengers.db.model.tables.records.PlayersRecord;

/**
 * JOOQ-based repository for Player entities. It provides methods to interact
 * with the Players table in the database.
 */
@Repository
@Transactional(readOnly = true)
public class PlayerJooqRepository {

	private final DSLContext dsl;

	public PlayerJooqRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	/**
	 * Finds Players by their username.
	 *
	 * @param username The username to search for.
	 * @return An Optional containing the Players entity if found, or empty
	 *         otherwise.
	 */
	public Optional<Players> findByUsername(String username) {
		return dsl.selectFrom(PLAYERS)
				.where(PLAYERS.USERNAME.eq(username))
				.fetchOptional()
				.map(r -> r.into(Players.class));
	}

	/**
	 * Finds Players by their ID.
	 *
	 * @param id The ID of the player.
	 * 
	 * @return An Optional containing the Players entity if found, or empty
	 *         otherwise.
	 */
	public Optional<Players> findById(long id) {
		return dsl.selectFrom(PLAYERS)
				.where(PLAYERS.ID.eq(id))
				.fetchOptional()
				.map(r -> r.into(Players.class));
	}

	/**
	 * Inserts a new Players entity into the database.
	 *
	 * @param newPlayer The Players entity to insert.
	 * 
	 * @return The inserted Players entity with any generated fields populated.
	 */
	public Players insert(Players newPlayer) {
		PlayersRecord playersRecord = dsl.newRecord(PLAYERS, newPlayer);
		playersRecord.insert();
		return playersRecord.into(Players.class);
	}
	
	/**
	 * Retrieves the current world ID for a given player ID.
	 *
	 * @param playerId The ID of the player.
	 * 
	 * @return An Optional containing the current world ID if found, or empty
	 *         otherwise.
	 */
	public Optional<Long> getCurrentWorldId(long playerId) {
	    return dsl.select(PLAYERS.CURRENT_WORLD_ID)
	            .from(PLAYERS)
	            .where(PLAYERS.ID.eq(playerId))
	            .fetchOptionalInto(Long.class);
	}
}
