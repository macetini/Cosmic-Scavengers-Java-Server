package com.cosmic.scavengers.networking;

import com.cosmic.scavengers.core.IMessageBroadcaster;
import com.cosmic.scavengers.db.UserRepository;
import com.cosmic.scavengers.db.UserService;
import com.cosmic.scavengers.engine.GameEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Initializes the Netty Channel Pipeline for new client connections. Sets up
 * decoders, encoders, and injects services into the GameChannelHandler.
 */
public class NettyServerInitializerHandler extends ChannelInitializer<SocketChannel> {

	// Dependencies passed from NettyServer
	private final GameEngine engine;
	private final UserRepository userRepository; // Currently unused, but kept for context
	private final IMessageBroadcaster broadcaster;
	private final UserService userService;

	public NettyServerInitializerHandler(GameEngine engine, UserRepository userRepository,
			IMessageBroadcaster broadcaster, UserService userService) {
		this.engine = engine;
		this.userRepository = userRepository;
		this.broadcaster = broadcaster;
		this.userService = userService;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// 1. Decoders (Inbound)
		pipeline.addLast("framer", new LineBasedFrameDecoder(8192));
		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));

		// 2. Encoder (Outbound)
		pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

		// 3. Application Logic Handler
		// Inject the required services into the GameChannelHandler
		pipeline.addLast("handler", new GameChannelHandler(userService, broadcaster));
	}
}