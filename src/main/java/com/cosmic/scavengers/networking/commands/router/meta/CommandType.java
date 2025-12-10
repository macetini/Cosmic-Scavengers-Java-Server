package com.cosmic.scavengers.networking.commands.router.meta;

public enum CommandType {
	TYPE_TEXT((byte) 0x01), TYPE_BINARY((byte) 0x02);
	
	private final byte value;

	CommandType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}