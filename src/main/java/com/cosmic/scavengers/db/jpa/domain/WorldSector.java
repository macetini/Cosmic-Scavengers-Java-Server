package com.cosmic.scavengers.db.jpa.domain;

import java.time.OffsetDateTime;
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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "world_sectors", uniqueConstraints = @UniqueConstraint(name = "world_sectors_unique_location", columnNames = {"world_id", "chunk_x", "chunk_y"}))
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WorldSector {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "world_id", nullable = false)
	private World world;

	@Column(name = "chunk_x", nullable = false)
	private Integer chunkX;

	@Column(name = "chunk_y", nullable = false)
	private Integer chunkY;

	@Column(name = "last_modified_at", nullable = false)
	private OffsetDateTime lastModifiedAt;

	@Column(name = "is_modified", nullable = false)
	private boolean isModified;

	@Convert(converter = JsonToMapConverter.class)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "sector_data", columnDefinition = "jsonb", nullable = false)
	private Map<String, Object> sectorData = new HashMap<>();

	public WorldSector() {
	}

	@PrePersist
	protected void onCreate() {
		if (lastModifiedAt == null) {
			lastModifiedAt = OffsetDateTime.now();
		}
	}

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

	public OffsetDateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(OffsetDateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean modified) {
		isModified = modified;
	}

	public Map<String, Object> getSectorData() {
		return sectorData;
	}

	public void setSectorData(Map<String, Object> sectorData) {
		this.sectorData = sectorData;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		WorldSector that = (WorldSector) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "WorldSector{" + "id=" + id + ", world=" + world + ", chunkX=" + chunkX + ", chunkY=" + chunkY + '}';
	}
}
