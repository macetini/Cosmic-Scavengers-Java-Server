package com.cosmic.scavengers.networking.requests.handlers;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosmic.scavengers.networking.meta.WorldData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldRequestHandler {
	private static final Logger log = LoggerFactory.getLogger(WorldRequestHandler.class);

	public static ByteBuf serializeWorldStateData(WorldData worldData) {
		int estimatedSize = 0;
		estimatedSize += Long.BYTES; // World ID (long)
		estimatedSize += Integer.BYTES; // World Name Length Prefix (int)
		estimatedSize += worldData.worldName().getBytes(StandardCharsets.UTF_8).length; // World Name (UTF-8 chars, 1 //
																						// bytes each)
		estimatedSize += Long.BYTES; // Map Seed (long)
		estimatedSize += Integer.BYTES; // Sector Size (int)

		ByteBuf buffer = Unpooled.buffer(estimatedSize);

		// 8 bytes: World ID (long)
		buffer.writeLong(worldData.id());

		// Variable Length String: World Name (Length Prefix)
		byte[] nameBytes = worldData.worldName().getBytes(StandardCharsets.UTF_8);
		buffer.writeInt(nameBytes.length); // 4 bytes for length
		buffer.writeBytes(nameBytes); // N bytes for data
		// 8 bytes: Map Seed (long)
		buffer.writeLong(worldData.mapSeed());
		// 4 bytes: Sector Size (int)
		buffer.writeInt(worldData.sectorSizeUnits());

		log.info("Serialized world state payload size: {} bytes.", buffer.readableBytes());
		return buffer;
	}
}
