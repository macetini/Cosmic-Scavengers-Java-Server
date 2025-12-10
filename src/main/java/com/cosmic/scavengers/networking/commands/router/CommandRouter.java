package com.cosmic.scavengers.networking.commands.router;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.core.commands.ICommandBinaryHandler;
import com.cosmic.scavengers.core.commands.ICommandTextHandler;
import com.cosmic.scavengers.networking.commands.NetworkBinaryCommands;
import com.cosmic.scavengers.networking.commands.NetworkTextCommands;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.PostConstruct;

/**
 * This component replaces the central 'switch' statement. It maps command codes
 * to their dedicated handler classes.
 */
@Component
public class CommandRouter {
	private static final Logger log = LoggerFactory.getLogger(CommandRouter.class);

	private Map<NetworkBinaryCommands, ICommandBinaryHandler> binaryCommandsMap;
	private final List<ICommandBinaryHandler> binaryHandlers;

	private Map<NetworkTextCommands, ICommandTextHandler> textCommandsMap;
	private final List<ICommandTextHandler> textHandlers;

	public CommandRouter(List<ICommandBinaryHandler> binaryCommands, List<ICommandTextHandler> textCommands) {
		this.binaryHandlers = binaryCommands;
		this.textHandlers = textCommands;
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
	 * 
	 * Executes the appropriate handler for the incoming command. This method is
	 * called from your Netty ChannelInboundHandler.
	 * 
	 * @param commandCode The command identifier read from the header.
	 * @param ctx         The channel context.
	 * @param payload     The message payload.
	 * 
	 */
	public void route(short commandCode, ChannelHandlerContext ctx, ByteBuf payload) {
		NetworkBinaryCommands command = NetworkBinaryCommands.fromCode(commandCode);
		if (command == null) {
			log.warn("Received unknown command code: 0x{}. Dropping payload.",
					Integer.toHexString(commandCode & 0xFFFF));
			payload.release();
			return;
		}

		ICommandBinaryHandler handler = binaryCommandsMap.get(command);

		log.info("Routing binary command: {}", command.getLogName());

		if (handler != null) {
			handler.handle(ctx, payload);
		} else {
			log.warn("No handler implemented for command: {}", command.getLogName());
			payload.release();
		}
	}

	public void route(String commandCode, ChannelHandlerContext ctx, ByteBuf payload) {
		NetworkTextCommands command = NetworkTextCommands.fromCode(commandCode);

		if (command == null) {
			log.warn("Received unknown text command code: '{}'. Dropping payload.", commandCode);
			payload.release();
			return;
		}

		log.info("Routing text command: {}", command.getLogName());

		ICommandTextHandler handler = textCommandsMap.get(command);

		if (handler != null) {
			handler.handle(ctx, payload);
		} else {
			log.warn("No text handler implemented for command: {}", command.getLogName());
			payload.release();
		}
	}

}