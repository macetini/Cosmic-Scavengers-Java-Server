package com.cosmic.scavengers.db.ingestion.conf;

public enum TraitsConf {
	DIRECTORY("traits");

	private final String key;

	private TraitsConf(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}
}
