package com.cosmic.scavengers.db.ingestion.conf;

public enum BlueprintsConf {
    DIRECTORY("entity_blueprints"),
    BEHAVIOR_CONFIGS("behavior_configs"),
    TRAIT_NAMES("trait_names"),
	TRAIT_OVERRIDES("trait_overrides");
    
    private final String key;
    
    public String key() { 
    	return key; 
    }
    
    private BlueprintsConf(String key) { 
    	this.key = key; 
    }
}
