package com.cosmic.scavengers.db.meta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity representing a Player account in the database. This class maps to
 * the 'players' table.
 */
@Entity
@Table(name = "players")
public class Player {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 50)
	private String username;

	// Stores the SHA-256 hash (64 characters)
	@Column(name = "password_hash", nullable = false, length = 64)
	private String passwordHash;

	// Stores the Base64 salt (24 characters)
	@Column(nullable = false, length = 24)
	private String salt;

	public Player() {
		// Default constructor required by JPA
	}

	public Player(String username, String passwordHash, String salt) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.salt = salt;
	}

	// --- Getters and Setters (Omitted for brevity, but necessary in Eclipse) ---
	// You must generate or manually add getters/setters for all fields (id,
	// username, passwordHash, salt)

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
}