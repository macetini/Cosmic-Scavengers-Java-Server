package com.cosmic.scavengers.db.meta;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Maps to 'player_attributes'. Stores strategic player metadata and links to
 * the main base. Uses @MapsId for a 1:1 relationship where the primary key is
 * also the foreign key.
 */
@Entity
@Table(name = "player_attributes")
public class PlayerAttribute {

	// Primary key is also the foreign key to Player
	@Id
	private Long playerId;

	@OneToOne
	@MapsId // Indicates that the primary key of this entity is mapped by the foreign key
			// association
	@JoinColumn(name = "player_id")
	private Player player;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_world_id", nullable = false)
	private World currentWorld;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_base_entity_id")
	private WorldEntity mainBaseEntity; // Can be NULL until the player establishes a base

	@Column(name = "total_kills", nullable = false)
	private Integer totalKills;

	// --- Constructors ---

	public PlayerAttribute() {
	}

	public PlayerAttribute(Player player, World currentWorld, WorldEntity mainBaseEntity, Integer totalKills) {
		this.player = player;
		this.currentWorld = currentWorld;
		this.mainBaseEntity = mainBaseEntity;
		this.totalKills = totalKills;
		if (player != null) {
			this.playerId = player.getId();
		}
	}

	// --- Getters and Setters ---

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (player != null) {
			this.playerId = player.getId();
		}
	}

	public World getCurrentWorld() {
		return currentWorld;
	}

	public void setCurrentWorld(World currentWorld) {
		this.currentWorld = currentWorld;
	}

	public WorldEntity getMainBaseEntity() {
		return mainBaseEntity;
	}

	public void setMainBaseEntity(WorldEntity mainBaseEntity) {
		this.mainBaseEntity = mainBaseEntity;
	}

	public Integer getTotalKills() {
		return totalKills;
	}

	public void setTotalKills(Integer totalKills) {
		this.totalKills = totalKills;
	}

	// --- Equals, HashCode, and ToString ---

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PlayerAttribute))
			return false;
		PlayerAttribute that = (PlayerAttribute) o;
		// Equality is based on the PlayerId for this 1:1 mapping
		return playerId != null && Objects.equals(playerId, that.playerId);
	}

	@Override
	public int hashCode() {
		return playerId != null ? Objects.hash(playerId) : 31;
	}

	@Override
	public String toString() {
		return "PlayerAttribute{" + "playerId=" + playerId + ", totalKills=" + totalKills + '}';
	}
}