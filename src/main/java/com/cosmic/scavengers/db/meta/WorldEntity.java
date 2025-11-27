package com.cosmic.scavengers.db.meta;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Maps to 'world_entities'. Stores static, persistent objects like bases and
 * resources.
 */
@Entity
@Table(name = "world_entities")
public class WorldEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "world_id", nullable = false)
	private World world;

	@Column(name = "entity_type", nullable = false, length = 50)
	private String entityType; // e.g., 'PLAYER_BASE', 'RESOURCE_NODE'

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_player_id")
	private Player ownerPlayer; // Nullable FK for ownership

	@Column(name = "chunk_x", nullable = false)
	private Integer chunkX;

	@Column(name = "chunk_y", nullable = false)
	private Integer chunkY;

	@Column(name = "pos_x", nullable = false)
	private Float posX;

	@Column(name = "pos_y", nullable = false)
	private Float posY;

	@Column(name = "current_health", nullable = false)
	private Integer currentHealth;

	// Maps the PostgreSQL jsonb type. We use String for generic storage.
	@Column(name = "state_data", columnDefinition = "jsonb")
	private String stateData;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "last_updated")
	private Instant lastUpdated;

	// --- Constructors ---

	public WorldEntity() {
	}

	public WorldEntity(World world, String entityType, Player ownerPlayer, Integer chunkX, Integer chunkY, Float posX,
			Float posY, Integer currentHealth, String stateData, Instant createdAt, Instant lastUpdated) {
		this.world = world;
		this.entityType = entityType;
		this.ownerPlayer = ownerPlayer;
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.posX = posX;
		this.posY = posY;
		this.currentHealth = currentHealth;
		this.stateData = stateData;
		this.createdAt = createdAt;
		this.lastUpdated = lastUpdated;
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Player getOwnerPlayer() {
		return ownerPlayer;
	}

	public void setOwnerPlayer(Player ownerPlayer) {
		this.ownerPlayer = ownerPlayer;
	}

	public Integer getChunkX() {
		return chunkX;
	}

	public void setChunkX(Integer chunkX) {
		this.chunkX = chunkX;
	}

	public Integer getChunkY() {
		return chunkY;
	}

	public void setChunkY(Integer chunkY) {
		this.chunkY = chunkY;
	}

	public Float getPosX() {
		return posX;
	}

	public void setPosX(Float posX) {
		this.posX = posX;
	}

	public Float getPosY() {
		return posY;
	}

	public void setPosY(Float posY) {
		this.posY = posY;
	}

	public Integer getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(Integer currentHealth) {
		this.currentHealth = currentHealth;
	}

	public String getStateData() {
		return stateData;
	}

	public void setStateData(String stateData) {
		this.stateData = stateData;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	// --- Equals, HashCode, and ToString ---

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WorldEntity))
			return false;
		WorldEntity that = (WorldEntity) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : 31;
	}

	@Override
	public String toString() {
		return "WorldEntity{" + "id=" + id + ", entityType='" + entityType + '\'' + ", chunkX=" + chunkX + ", chunkY="
				+ chunkY + '}';
	}
}