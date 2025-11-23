package com.cosmic.scavengers.db.meta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA Entity representing any persistent in-game object (unit, building, item)
 * owned by a player. This maps to the 'player_entities' table. * This design is
 * typical for a Component-Entity approach where 'players' are accounts and
 * 'player_entities' are the physical game objects they control.
 */
@Entity
@Table(name = "player_entities")
public class PlayerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Foreign key linking back to the Player account (the owner)
	@Column(name = "player_id", nullable = false)
	private Long playerId;

	// Identifies the type of game object (e.g., "SOLDIER", "BUILDING", "STARSHIP")
	@Column(name = "entity_type", nullable = false, length = 50)
	private String entityType;

	// World position coordinates (double maps well to float8 in PostgreSQL)
	@Column(name = "pos_x", nullable = false)
	private double posX;

	@Column(name = "pos_y", nullable = false)
	private double posY;

	// Current health/durability
	@Column(name = "health", nullable = false)
	private Integer health;

	// Flexible column for complex or evolving state data (mapped from PostgreSQL
	// jsonb)
	@Column(name = "state_data", columnDefinition = "jsonb")
	private String stateData; // Stored as raw JSON string

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	public PlayerEntity() {
		// Default constructor required by JPA
	}

	public PlayerEntity(Long playerId, String entityType, double posX, double posY, Integer health, String stateData) {
		this.playerId = playerId;
		this.entityType = entityType;
		this.posX = posX;
		this.posY = posY;
		this.health = health;
		this.stateData = stateData;
		this.createdAt = LocalDateTime.now();
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public Integer getHealth() {
		return health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}

	public String getStateData() {
		return stateData;
	}

	public void setStateData(String stateData) {
		this.stateData = stateData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}