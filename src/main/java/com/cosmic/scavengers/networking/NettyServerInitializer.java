package com.cosmic.scavengers.networking;

import com.cosmic.scavengers.services.UserService;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
	private final UserService userService;

	public NettyServerInitializer(UserService userService) {
		this.userService = userService;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(1048576, // maxFrameLength (1MB)
				0, // lengthFieldOffset
				4, // lengthFieldLength (int size)
				0, // lengthAdjustment
				4 // initialBytesToStrip (removes the length prefix before passing to handler)
		));

		pipeline.addLast("prepender", new LengthFieldPrepender(4));
		pipeline.addLast("handler", new GameChannelHandler(userService));
	}
}