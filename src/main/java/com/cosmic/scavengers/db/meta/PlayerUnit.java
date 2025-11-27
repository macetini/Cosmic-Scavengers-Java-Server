package com.cosmic.scavengers.db.meta;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * Maps to 'player_units'. Stores strategic unit movement and garrison state.
 */
@Entity
@Table(name = "player_units")
public class PlayerUnit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_player_id", nullable = false)
	private Player ownerPlayer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "world_id", nullable = false)
	private World world;

	@Column(name = "unit_type", nullable = false, length = 50)
	private String unitType; // 'FIGHTER_SQUAD', 'TANK_CORP'

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_base_id")
	private WorldEntity currentBase; // Nullable: unit is in transit if null

	// Strategic Movement Fields (used when currentBase IS NULL)
	@Column(name = "is_in_transit", nullable = false)
	private Boolean isInTransit;

	@Column(name = "target_chunk_x")
	private Integer targetChunkX;

	@Column(name = "target_chunk_y")
	private Integer targetChunkY;

	@Column(name = "arrival_time")
	private Instant arrivalTime; // Server combat scheduler uses this

	// Maps the PostgreSQL jsonb type.
	@Column(name = "state_data", columnDefinition = "jsonb")
	private String stateData;

	@Column(name = "created_at")
	private Instant createdAt;

	// --- Constructors ---

	public PlayerUnit() {
	}

	public PlayerUnit(Player ownerPlayer, World world, String unitType, Integer quantity, WorldEntity currentBase,
			Boolean isInTransit, Integer targetChunkX, Integer targetChunkY, Instant arrivalTime, String stateData,
			Instant createdAt) {
		this.ownerPlayer = ownerPlayer;
		this.world = world;
		this.unitType = unitType;
		this.quantity = quantity;
		this.currentBase = currentBase;
		this.isInTransit = isInTransit;
		this.targetChunkX = targetChunkX;
		this.targetChunkY = targetChunkY;
		this.arrivalTime = arrivalTime;
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

	public Player getOwnerPlayer() {
		return ownerPlayer;
	}

	public void setOwnerPlayer(Player ownerPlayer) {
		this.ownerPlayer = ownerPlayer;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public WorldEntity getCurrentBase() {
		return currentBase;
	}

	public void setCurrentBase(WorldEntity currentBase) {
		this.currentBase = currentBase;
	}

	public Boolean getInTransit() {
		return isInTransit;
	}

	public void setInTransit(Boolean inTransit) {
		isInTransit = inTransit;
	}

	public Integer getTargetChunkX() {
		return targetChunkX;
	}

	public void setTargetChunkX(Integer targetChunkX) {
		this.targetChunkX = targetChunkX;
	}

	public Integer getTargetChunkY() {
		return targetChunkY;
	}

	public void setTargetChunkY(Integer targetChunkY) {
		this.targetChunkY = targetChunkY;
	}

	public Instant getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Instant arrivalTime) {
		this.arrivalTime = arrivalTime;
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
		if (!(o instanceof PlayerUnit))
			return false;
		PlayerUnit that = (PlayerUnit) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : 31;
	}

	@Override
	public String toString() {
		return "PlayerUnit{" + "id=" + id + ", unitType='" + unitType + '\'' + ", quantity=" + quantity
				+ ", isInTransit=" + isInTransit + '}';
	}
}