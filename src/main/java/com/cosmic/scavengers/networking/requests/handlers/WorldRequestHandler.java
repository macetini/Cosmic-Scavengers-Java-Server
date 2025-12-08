package com.cosmic.scavengers.networking.requests.handlers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosmic.scavengers.db.model.tables.pojos.PlayerEntities;
import com.cosmic.scavengers.db.model.tables.pojos.Worlds;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WorldRequestHandler {
	private static final Logger log = LoggerFactory.getLogger(WorldRequestHandler.class);

	private WorldRequestHandler() {
		// Private constructor to prevent instantiation
	}

	public static ByteBuf serializeWorldStateData(Worlds worldData) {
		if (worldData == null) {
			throw new IllegalArgumentException("WorldData cannot be null.");
		}

		int estimatedSize = 0;
		estimatedSize += Long.BYTES; // World ID (long)
		estimatedSize += Integer.BYTES; // World Name Length Prefix (int)
		estimatedSize += worldData.getWorldName().getBytes(StandardCharsets.UTF_8).length; // World Name (UTF-8 chars, 1
																							// //
																							// bytes each)
		estimatedSize += Long.BYTES; // Map Seed (long)
		estimatedSize += Integer.BYTES; // Sector Size (int)

		ByteBuf buffer = Unpooled.buffer(estimatedSize);

		// 8 bytes: World ID (long)
		buffer.writeLong(worldData.getId());

		// Variable Length String: World Name (Length Prefix)
		byte[] nameBytes = worldData.getWorldName().getBytes(StandardCharsets.UTF_8);
		buffer.writeInt(nameBytes.length); // 4 bytes for length
		buffer.writeBytes(nameBytes); // N bytes for data
		// 8 bytes: Map Seed (long)
		buffer.writeLong(worldData.getMapSeed());
		// 4 bytes: Sector Size (int)
		buffer.writeInt(worldData.getSectorSizeUnits());

		log.info("Serialized world state payload size: {} bytes.", buffer.readableBytes());
		return buffer;
	}

	public static ByteBuf serializePlayerEntities(List<PlayerEntities> entities) {
		if (entities == null) {
			throw new IllegalArgumentException("Player entities list cannot be null.");
		}

		int estimatedSize = 0;
		estimatedSize += Integer.BYTES; // Number of Entities (int)

		int numEntities = entities.size();
		int fixedSize = Long.BYTES + // id
		// Long.BYTES + // playerId
		// Long.BYTES + // worldId

				Integer.BYTES + // chunkX
				Integer.BYTES + // chunkY

				Float.BYTES + // posX
				Float.BYTES + // posY

				Integer.BYTES; // health

		estimatedSize += numEntities * fixedSize;

		ByteBuf buffer = Unpooled.buffer(estimatedSize);

		buffer.writeInt(numEntities);

		for (int i = 0; i < numEntities; i++) {
			PlayerEntities entity = entities.get(i);

			buffer.writeLong(entity.getId());
			// buffer.writeLong(entity.playerId());
			// buffer.writeLong(entity.worldId());

			buffer.writeInt(entity.getChunkX());
			buffer.writeInt(entity.getChunkY());

			buffer.writeFloat(entity.getPosX());
			buffer.writeFloat(entity.getPosY());

			buffer.writeInt(entity.getHealth());
		}

		log.info("Serialized player entities payload size: {} bytes for {} entities.", buffer.readableBytes(),
				numEntities);

		return buffer;
	}
}
