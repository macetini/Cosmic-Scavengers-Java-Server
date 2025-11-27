package com.cosmic.scavengers.db.meta;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Maps to 'player_entities'. Stores dynamic, real-time locations of the
 * player's or avatar. This class MUST contain the 'world' property for the
 * inverse mapping to succeed.
 */
@Entity
@Table(name = "player_entities")
public class PlayerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "player_id", nullable = false)
	private Player player;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "world_id", nullable = false)
	private World world;

	@Column(name = "entity_type", nullable = false, length = 50)
	private String entityType; // 'SHIP', 'AVATAR'

	@Column(name = "chunk_x", nullable = false)
	private Integer chunkX;

	@Column(name = "chunk_y", nullable = false)
	private Integer chunkY;

	@Column(name = "pos_x", nullable = false)
	private Float posX;

	@Column(name = "pos_y", nullable = false)
	private Float posY;

	@Column(name = "health", nullable = false)
	private Integer health;

	// Maps the PostgreSQL jsonb type.
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "state_data", columnDefinition = "jsonb")
	private String stateData;

	@Column(name = "created_at")
	private Instant createdAt;

	// --- Constructors ---

	public PlayerEntity() {
	}

	public PlayerEntity(Player player, World world, String entityType, Integer chunkX, Integer chunkY, Float posX,
			Float posY, Integer health, String stateData, Instant createdAt) {
		this.player = player;
		this.world = world;
		this.entityType = entityType;
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.posX = posX;
		this.posY = posY;
		this.health = health;
		this.stateData = stateData;
		this.createdAt = createdAt;		
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
	// --- Equals, HashCode, and ToString ---

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PlayerEntity))
			return false;
		PlayerEntity that = (PlayerEntity) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : 31;
	}

	@Override
	public String toString() {
		return "PlayerEntity{" + "id=" + id + ", entityType='" + entityType + '\'' + ", chunkX=" + chunkX + ", chunkY="
				+ chunkY + '}';
	}
}