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
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
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

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	
	private final CommandRouter commandRouter;

	public NettyServer(CommandRouter commandRouter) {
        this.commandRouter = commandRouter;        
    }

	@Override
	public void run() {
		log.debug("Netty Server Thread started successfully. Initializing Event Loops.");
		try {
			// Responsible only for accepting incoming connections and handing them off to
			// the workerGroup. Does not need more than one thread, as accepting connections
			// is not a heavy task.
			bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
			workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128) // TCP backlog size
					.childOption(ChannelOption.SO_KEEPALIVE, true) // TCP keep-alive
					.childHandler(new NettyServerInitializer(commandRouter));

			log.debug("Attempting to bind Netty to port: {}", gamePort);
			ChannelFuture future = serverBootstrap.bind(gamePort).sync();
			log.debug("Netty live. Listening on port: {}", gamePort);

			// Wait until the server socket is closed.
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("Netty Server thread interrupted: {}", e.getMessage());
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			log.error("Unexpected error in Netty Server: {}", e.getMessage(), e);
		} finally {
			shutdown();
		}
	}

	@PreDestroy
	public void shutdown() {
		log.info("Shutting down Netty event groups gracefully.");
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
	}
}
