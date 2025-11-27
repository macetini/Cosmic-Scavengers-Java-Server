package com.cosmic.scavengers.db.meta;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * Maps to the 'worlds' table, defining the properties of a game map.
 */
@Entity
@Table(name = "worlds")
public class World {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "world_name", nullable = false, length = 50, unique = true)
	private String worldName;

	@Column(name = "map_seed", nullable = false)
	private Long mapSeed; // Crucial for procedural generation

	@Column(name = "sector_size_units", nullable = false)
	private Integer sectorSizeUnits;

	// Relationships
	@OneToMany(mappedBy = "world")
	private Set<WorldEntity> entities;

	@OneToMany(mappedBy = "world")
	private Set<PlayerEntity> playerEntities;

	@OneToMany(mappedBy = "world")
	private Set<PlayerUnit> units;

	// --- Constructors ---

	public World() {
	}

	public World(String worldName, Long mapSeed, Integer sectorSizeUnits) {
		this.worldName = worldName;
		this.mapSeed = mapSeed;
		this.sectorSizeUnits = sectorSizeUnits;
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public Long getMapSeed() {
		return mapSeed;
	}

	public void setMapSeed(Long mapSeed) {
		this.mapSeed = mapSeed;
	}

	public Integer getSectorSizeUnits() {
		return sectorSizeUnits;
	}

	public void setSectorSizeUnits(Integer sectorSizeUnits) {
		this.sectorSizeUnits = sectorSizeUnits;
	}

	public Set<WorldEntity> getEntities() {
		return entities;
	}

	public void setEntities(Set<WorldEntity> entities) {
		this.entities = entities;
	}

	public Set<PlayerEntity> getPlayerEntities() {
		return playerEntities;
	}

	public void setPlayerEntities(Set<PlayerEntity> playerEntities) {
		this.playerEntities = playerEntities;
	}

	public Set<PlayerUnit> getUnits() {
		return units;
	}

	public void setUnits(Set<PlayerUnit> units) {
		this.units = units;
	}

	// --- Equals, HashCode, and ToString ---

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof World))
			return false;
		World world = (World) o;
		return id != null && Objects.equals(id, world.id);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : 31;
	}

	@Override
	public String toString() {
		return "World{" + "id=" + id + ", worldName='" + worldName + '\'' + ", mapSeed=" + mapSeed + '}';
	}
}