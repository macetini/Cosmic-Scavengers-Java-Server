package com.cosmic.scavengers.db.ingestion.exceptions;

/**
 * Thrown when blueprint data mapping fails during ingestion. Indicates that
 * YAML blueprint configuration could not be converted to EntityBlueprint.
 */
public class IngestionMappingException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 15539914371561977L;

	public IngestionMappingException(String message) {
		super(message);
	}

	public IngestionMappingException(String message, Throwable cause) {
		super(message, cause);
	}
}
