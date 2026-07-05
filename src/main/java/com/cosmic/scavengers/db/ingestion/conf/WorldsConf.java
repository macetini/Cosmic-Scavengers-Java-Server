package com.cosmic.scavengers.db.ingestion.conf;

public enum WorldsConf {
	DIRECTORY("worlds");

	private final String key;

	private WorldsConf(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}
}