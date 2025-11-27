package com.cosmic.scavengers.db.meta;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Maps to the 'players' table, managing user authentication details.
 */
@Entity
@Table(name = "players")
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, length = 50, unique = true)
	private String username;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "salt", nullable = false)
	private String salt;

	@Column(name = "created_at")
	private Instant createdAt;

	// Relationships (One-to-Many from Player)
	@OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
	private PlayerAttribute attributes;

	@OneToMany(mappedBy = "ownerPlayer")
	private Set<WorldEntity> ownedWorldEntities;

	@OneToMany(mappedBy = "ownerPlayer")
	private Set<PlayerUnit> units;

	// --- Constructors ---

	public Player() {
	}

	public Player(String username, String passwordHash, String salt, Instant createdAt) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.createdAt = createdAt;
	}

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public PlayerAttribute getAttributes() {
		return attributes;
	}

	public void setAttributes(PlayerAttribute attributes) {
		this.attributes = attributes;
	}

	public Set<WorldEntity> getOwnedWorldEntities() {
		return ownedWorldEntities;
	}

	public void setOwnedWorldEntities(Set<WorldEntity> ownedWorldEntities) {
		this.ownedWorldEntities = ownedWorldEntities;
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
		if (!(o instanceof Player))
			return false;
		Player player = (Player) o;
		// Use ID for equality check if it exists, otherwise rely on object identity
		return id != null && Objects.equals(id, player.id);
	}

	@Override
	public int hashCode() {
		// Use a constant or a calculation based on ID if ID exists
		return id != null ? Objects.hash(id) : 31;
	}

	@Override
	public String toString() {
		return "Player{" + "id=" + id + ", username='" + username + '\'' + ", createdAt=" + createdAt + '}';
	}
}