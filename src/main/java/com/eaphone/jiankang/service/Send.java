package com.eaphone.jiankang.service;

import com.eaphone.jiankang.handle.DiscardClientHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class Send {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new StringDecoder());
                        sc.pipeline().addLast(new DiscardClientHandle());
                    }
                });
        ChannelFuture f=b.connect("127.0.0.1",8080).sync();
        f.channel().writeAndFlush(Unpooled.copiedBuffer("{id:'e0ca94d60b9d0000-365c-00000001-07c584f712f21feb-c1bb6541',msg:'hi'}".getBytes()));
        f.channel().closeFuture().sync();
        worker.shutdownGracefully();
    }
}
