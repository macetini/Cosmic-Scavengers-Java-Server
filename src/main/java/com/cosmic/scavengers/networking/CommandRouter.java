package com.cosmic.scavengers.networking;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.core.commands.ICommandTextHandler;
import com.cosmic.scavengers.networking.commands.CommandType;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommand;
import com.cosmic.scavengers.networking.commands.NetworkTextCommand;
import com.cosmic.scavengers.networking.constants.NetworkAttributeKeys;
import com.google.protobuf.GeneratedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import jakarta.annotation.PostConstruct;

/**
 * This component replaces the central 'switch' statement. It maps command codes
 * to their dedicated handler classes.
 */
@Component
public class CommandRouter {
	private static final Logger log = LoggerFactory.getLogger(CommandRouter.class);	
	
	private Map<NetworkBinaryCommand, ICommandBinaryHandler> binaryCommandsMap;
	private final List<ICommandBinaryHandler> binaryHandlers;

	private Map<NetworkTextCommand, ICommandTextHandler> textCommandsMap;
	private final List<ICommandTextHandler> textHandlers;
	
	private final ChannelRegistry channelRegistry;
	private final MessageDispatcher messageDispatcher;

	private static final String TEXT_COMMAND_DELIMITER = "\\|";

	public CommandRouter(List<ICommandBinaryHandler> binaryCommands,
			List<ICommandTextHandler> textCommands,
			ChannelRegistry channelRegistry,
			MessageDispatcher messageDispatcher) {
		this.binaryHandlers = binaryCommands;
		this.textHandlers = textCommands;
		this.channelRegistry = channelRegistry;
		this.messageDispatcher = messageDispatcher;
	}
	
	public void addChannel(Long playerId, ChannelHandlerContext ctx) {
		channelRegistry.add(playerId, ctx);
	}
	
	public void removeChannel(ChannelHandlerContext ctx) {
		channelRegistry.remove(ctx);
	}

	/**
	 * Initializes the command map after all handlers have been injected.
	 */
	@PostConstruct
	public void init() {
		binaryCommandsMap = binaryHandlers.stream()
				.collect(Collectors.toMap(ICommandBinaryHandler::getCommand, Function.identity()));
		log.info("Initialized Network Command Router with {} Binary handlers.", binaryCommandsMap.size());

		textCommandsMap = textHandlers.stream()
				.collect(Collectors.toMap(ICommandTextHandler::getCommand, Function.identity()));
		log.info("Initialized Network Command Router with {} Text handlers.", textCommandsMap.size());
	}

	/**
	 * Routes a command to the correct handler.
	 * 
	 * @param ctx     The Netty ChannelHandlerContext.
	 * @param command The Command Payload.
	 * 
	 */
	public void routeIncoming(ChannelHandlerContext ctx, ByteBuf command) {
		byte commandValue = command.readByte();
		CommandType commandType = CommandType.fromValue(commandValue);
		switch (commandType) {
		case TYPE_TEXT:
			routeTextCommand(ctx, command);
			break;
		case TYPE_BINARY:
			routeBinaryCommand(ctx, command);
			break;
		case TYPE_UNKNOWN:
			log.warn("Received unknown message type: {}", commandType);
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + commandType);
		}
	}

	private void routeTextCommand(ChannelHandlerContext ctx, ByteBuf payload) {
		String message = payload.toString(CharsetUtil.UTF_8).trim();
		String[] parts = message.split(TEXT_COMMAND_DELIMITER);
		if (parts.length == 0) {
			log.warn("Received empty text command.");
			return;
		}
		String commandCode = parts[0];
		NetworkTextCommand command = NetworkTextCommand.fromCode(commandCode);

		if (command == null) {
			log.warn("Received unknown text command code: '{}'. Dropping payload.", commandCode);
			payload.release();
			return;
		}

		log.info("Routing text command: {}", command.getLogName());
		ICommandTextHandler handler = textCommandsMap.get(command);

		if (handler != null) {
			handler.handle(ctx, parts);
		} else {
			log.warn("No text handler implemented for command: {}", command.getLogName());
			payload.release();
		}
	}

	private void routeBinaryCommand(ChannelHandlerContext ctx, ByteBuf payload) {
		if (!hasMinimumBinaryPayload(payload)) {
			log.warn("Binary Payload too short to contain command.");
			return;
		}

		short commandCode = payload.readShort();
		NetworkBinaryCommand command = resolveBinaryCommand(commandCode);
		if (command == null) {			
			log.error("Received unknown command code: '0x{}'. Dropping payload.",
					Integer.toHexString(commandCode & 0xFFFF));
			payload.release();
			return;
		}

		if (log.isTraceEnabled()) {
			log.trace("Routing [Inbound BINARY] Command | Code: [{}] | Log: [{}]",
					Integer.toHexString(commandCode & 0xFFFF), command.getLogText());
		}

		ICommandBinaryHandler handler = binaryCommandsMap.get(command);
		if (handler == null) {
			log.warn("No Handler implemented for [Inbound Command] | Log: [{}]", command.getLogText());
			payload.release();
			return;
		}

		Long playerId = validatePlayerId(ctx, payload);
		if(playerId == null) {
			payload.release();
			return;
		}

		this.channelRegistry.add(playerId, ctx);
		handler.handle(playerId, payload);
	}

	private boolean hasMinimumBinaryPayload(ByteBuf payload) {
		return payload.readableBytes() >= 2;
	}

	private NetworkBinaryCommand resolveBinaryCommand(short commandCode) {
		return NetworkBinaryCommand.fromCode(commandCode);
	}
	
	private Long validatePlayerId(ChannelHandlerContext ctx, ByteBuf payload) {
		Long playerId = (Long) ctx.channel().attr(NetworkAttributeKeys.PLAYER_ID.getKey()).get();
		if (playerId == null) {
			throw new IllegalStateException("Player ID not found in channel attributes for channel: " + ctx.channel().id());
		}
		
		if (!isPayloadPlayerIdValid(payload, playerId)) {
			return null;
		}
		return playerId;
	}

	private boolean isPayloadPlayerIdValid(ByteBuf payload, Long playerId) {
		long payloadPlayerId = payload.readLong();
		if (!playerId.equals(payloadPlayerId)) {
			log.error("Player ID mismatch: Channel PlayerId '{}' does not match Payload PlayerId '{}'.", playerId, payloadPlayerId);
			return false;
		}
		return true;
	}
	
	public void routeOutbound(Long playerId, CommandType commandType, NetworkBinaryCommand command, GeneratedMessage message) {		
		ChannelHandlerContext ctx = channelRegistry.get(playerId);
		routeOutbound(ctx, commandType, command, message);
	}
	
	public void routeOutbound(ChannelHandlerContext ctx, CommandType commandType, NetworkBinaryCommand command, GeneratedMessage message) {		
		switch (commandType) {
		case TYPE_TEXT:
			log.trace("Routing [Outbound TEXT] Command | Type: [{}]", commandType);
			break;
		case TYPE_BINARY:		
			log.trace("Routing [Outbound BINARY] Command | Type: [{}]", commandType);
			messageDispatcher.sendBinaryProtobufMessage(ctx, message, command.getCode());
			break;
		case TYPE_UNKNOWN:
			log.warn("Routing [Outbound UNKNOWN] Command | Type: [{}]", commandType);
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + commandType);
		}
	}
}