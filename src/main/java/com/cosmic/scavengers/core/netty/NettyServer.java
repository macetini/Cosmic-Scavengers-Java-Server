package com.cosmic.scavengers.core.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cosmic.scavengers.networking.CommandRouter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;

/**
 * Placeholder for the Netty server setup and binding.
 */
@Component
public class NettyServer implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

	@Value("${game.server.port:9001}")
	private int gamePort;

	private final CommandRouter networkDispatcher;

	// Keep references so we can shut them down from @PreDestroy as well
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private io.netty.channel.Channel serverChannel;

	public NettyServer(CommandRouter networkDispatcher) {
		this.networkDispatcher = networkDispatcher;
	}

	@Override
	public void run() {
		log.info("[Netty Thread] Thread started successfully. Initializing Event Loops...");
		// Validate port
		if (gamePort <= 0 || gamePort > 65535) {
			log.error("Configured game port {} is invalid. Must be in range 1-65535.", gamePort);
			return;
		}

		try {
			// boss: single thread for accepting connections; worker: default threads
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup();

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new NettyServerInitializer(networkDispatcher));

			log.info("Attempting to bind Netty to port: {}", gamePort);
			ChannelFuture future = serverBootstrap.bind(gamePort).sync();
			serverChannel = future.channel();
			log.info("Netty live. Listening on port: {}", gamePort);

			// Wait until the server socket is closed.
			serverChannel.closeFuture().sync();

		} catch (InterruptedException e) {
			log.error("Netty Server thread interrupted", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			log.error("Unexpected error in Netty Server", e);
		} finally {
			log.info("Shutting down Netty event groups gracefully.");
			if (serverChannel != null && serverChannel.isOpen()) {
				try {
					serverChannel.close().syncUninterruptibly();
				} catch (Exception e) {
					log.warn("Error closing server channel: {}", e.getMessage());
				}
			}
			if (bossGroup != null) {
				bossGroup.shutdownGracefully().syncUninterruptibly();
			}
			if (workerGroup != null) {
				workerGroup.shutdownGracefully().syncUninterruptibly();
			}
		}
	}

	@PreDestroy
	public void shutdown() {
		log.info("PreDestroy invoked: shutting down Netty server if running.");

		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				serverChannel.close().syncUninterruptibly();
			} catch (Exception e) {
				log.warn("Error closing server channel during PreDestroy: {}", e.getMessage());
			}
		}

		if (bossGroup != null) {
			bossGroup.shutdownGracefully().syncUninterruptibly();
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully().syncUninterruptibly();
		}
	}
}
