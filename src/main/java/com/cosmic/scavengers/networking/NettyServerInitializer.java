package com.cosmic.scavengers.networking;

import com.cosmic.scavengers.services.UserService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

	private final UserService userService;

	private static final int MAX_FRAME_LENGTH = 1024 * 1024;
	private static final int LENGTH_FIELD_LENGTH = 4;

	public NettyServerInitializer(UserService userService) {
		this.userService = userService;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0,
				LENGTH_FIELD_LENGTH, 0, LENGTH_FIELD_LENGTH);

		GameChannelHandler handler = new GameChannelHandler(userService);

		ch.pipeline().addLast(decoder, handler);
	}
}