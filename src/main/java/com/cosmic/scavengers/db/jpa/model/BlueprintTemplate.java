package com.cosmic.scavengers.db.jpa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A "Hot" immutable template for creating entities. Uses Java 17 Record for
 * performance and conciseness.
 */
public record BlueprintTemplate(
		String id, 
		String categoryId, 
		int baseHealth, 
		boolean isStatic, 
		List<String> traitIds,
		Map<String, Map<String, Object>> traitOverrides,
		Map<String, Map<String, Object>> traitValues,
		List<String> initialBuffIds) 
	{
	public BlueprintTemplate {
		traitIds = List.copyOf(traitIds != null ? traitIds : List.of());
		traitOverrides = traitOverrides != null ? Map.copyOf(traitOverrides) : Map.of();		
		traitValues = traitValues != null ? new HashMap<>(traitValues) : new HashMap<>();
		initialBuffIds = List.copyOf(initialBuffIds != null ? initialBuffIds : List.of());
	}
}
