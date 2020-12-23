package com.eaphone.jiankang.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eaphone.jiankang.handle.DiscardClientHandle;
import com.eaphone.jiankang.handle.DiscardServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.stereotype.Service;


@Service
public class NettyService {
    public void service(ChannelHandlerContext ctx, Object msg) {
        System.out.println(ctx.name() + ":" + String.valueOf(msg));
        JSONObject jsonObject = JSON.parseObject((String) msg);
        String id = jsonObject.getString("id");
        String msg1 = jsonObject.getString("msg");

        ChannelHandlerContext channelHandlerContext = DiscardServerHandler.cmap.get(id);
        ChannelFuture future = channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(msg1.getBytes()));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()){
                    ctx.writeAndFlush(Unpooled.copiedBuffer("true".getBytes()));
                }else{
                    ctx.writeAndFlush(Unpooled.copiedBuffer("false".getBytes()));
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                socket1();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void socket1() throws Exception {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                       // ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                        //sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
                        sc.pipeline().addLast(new StringDecoder());
                        sc.pipeline().addLast(new DiscardClientHandle());
                    }
                });
        ChannelFuture f=b.connect("127.0.0.1",8080).sync();
        f.channel().closeFuture().sync();
        worker.shutdownGracefully();
    }
}
