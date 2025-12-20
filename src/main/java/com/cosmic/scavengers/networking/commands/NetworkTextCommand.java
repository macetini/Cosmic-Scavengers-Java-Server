package com.cosmic.scavengers.networking.commands;

import java.util.HashMap;
import java.util.Map;

public enum NetworkTextCommand {
	C_CONNECT("C_CONNECT", "Client requests to connect to the server"),
	S_CONNECT_PASS("S_CONNECT_PASS", "Server acknowledges client connection"),

	C_LOGIN("C_LOGIN", "Client requests to log in with credentials"),
	S_LOGIN_PASS("S_LOGIN_PASS", "Server responds to client login attempt"),
	S_LOGIN_FAIL("S_LOGIN_FAIL", "Server informs client of failed login attempt"),

	C_REGISTER("C_REGISTER", "Client requests to register a new account"),
	S_REGISTER_OK("S_REGISTER_OK", "Server responds to client registration attempt"),
	S_REGISTER_FAIL("S_REGISTER_FAIL", "Server informs client of failed registration attempt");

	private static final Map<String, NetworkTextCommand> BY_CODE = new HashMap<>();
	static {		
		for (NetworkTextCommand command : NetworkTextCommand.values()) {
			BY_CODE.put(command.getCode(), command);
		}
	}

	private final String code;
	private final String description;

	NetworkTextCommand(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static NetworkTextCommand fromCode(String code) {
		return BY_CODE.get(code);
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Helper for logging to get both the name and the numerical code.
	 */
	public String getLogName() {
		return String.format("name: '%s' - code: '%s' - desc: '%s'", this.name(), this.getCode(),
				this.getDescription());
	}

}
