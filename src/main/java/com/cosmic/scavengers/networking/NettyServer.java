package com.cosmic.scavengers.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosmic.scavengers.core.IMessageBroadcaster;
import com.cosmic.scavengers.db.UserRepository;
import com.cosmic.scavengers.engine.GameEngine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Placeholder for the Netty server setup and binding.
 */
public class NettyServer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
	private final int port = 8080;

	// Dependencies
	private final GameEngine engine;
	private final UserRepository userRepository;
	private final IMessageBroadcaster broadcaster;

	public NettyServer(GameEngine engine, UserRepository userRepository, IMessageBroadcaster broadcaster) {
		this.engine = engine;
		this.userRepository = userRepository;
		this.broadcaster = broadcaster;
	}

	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new NettyServerInitializerHandler(engine, userRepository, broadcaster)); // Placeholder

			b.bind(port).sync().channel().closeFuture().sync();
			log.info("Netty Server started and listening on port {}", port);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	// Placeholder class needed for compilation
	private static class NettyServerInitializerHandler
			extends io.netty.channel.ChannelInitializer<io.netty.channel.socket.SocketChannel> {
		private final GameEngine engine;
		private final UserRepository userRepository;
		private final IMessageBroadcaster broadcaster;

		public NettyServerInitializerHandler(GameEngine engine, UserRepository userRepository,
				IMessageBroadcaster broadcaster) {
			this.engine = engine;
			this.userRepository = userRepository;
			this.broadcaster = broadcaster;
		}

		@Override
		protected void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
			// Setup Netty handlers here (e.g., decoders, encoders, GameChannelHandler)
		}
	}
}