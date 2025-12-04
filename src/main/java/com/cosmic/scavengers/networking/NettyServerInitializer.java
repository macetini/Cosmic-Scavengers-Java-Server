package com.cosmic.scavengers.networking;

import com.cosmic.scavengers.services.PlayerStateService;
import com.cosmic.scavengers.services.UserService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

	private final UserService userService;
	private final PlayerStateService playerStateService;

	private static final int MAX_FRAME_LENGTH = 1024 * 1024;
	private static final int LENGTH_FIELD_LENGTH = 4;

	public NettyServerInitializer(UserService userService, PlayerStateService playerStateService) {
		this.userService = userService;
		this.playerStateService = playerStateService;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0,
				LENGTH_FIELD_LENGTH, 0, LENGTH_FIELD_LENGTH);

		LengthFieldPrepender prepender = new LengthFieldPrepender(LENGTH_FIELD_LENGTH);
		GameChannelHandler handler = new GameChannelHandler(userService, playerStateService);
		ch.pipeline().addLast(decoder, prepender, handler);
	}
}