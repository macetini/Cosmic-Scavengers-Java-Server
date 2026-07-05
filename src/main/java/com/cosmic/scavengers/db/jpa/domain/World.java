package com.cosmic.scavengers.db.jpa.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.cosmic.scavengers.core.yaml.JsonToMapConverter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "worlds", uniqueConstraints = @UniqueConstraint(name = "worlds_world_name_key", columnNames = "world_name"))
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class World {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "world_name", nullable = false, length = 50, unique = true)
	private String worldName;

	@Column(name = "map_seed", nullable = false)
	private Long mapSeed;

	@Column(name = "sector_size_units", nullable = false)
	private Integer sectorSizeUnits = 1000;

	@Convert(converter = JsonToMapConverter.class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "generation_config", columnDefinition = "jsonb", nullable = false)
	private Map<String, Object> generationConfig = new HashMap<>();

	public World() {
	}

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

	public Map<String, Object> getGenerationConfig() {
		return generationConfig;
	}

	public void setGenerationConfig(Map<String, Object> generationConfig) {
		this.generationConfig = generationConfig;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		World world = (World) o;
		return Objects.equals(id, world.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "World{" + "id=" + id + ", worldName='" + worldName + '\'' + ", mapSeed=" + mapSeed + '}';
	}
}
