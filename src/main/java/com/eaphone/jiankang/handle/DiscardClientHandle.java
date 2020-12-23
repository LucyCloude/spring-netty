package com.eaphone.jiankang.handle;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.springframework.stereotype.Component;

public class DiscardClientHandle extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(String.valueOf(msg));
        super.channelRead(ctx, msg);
        ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(String.valueOf(msg).getBytes()));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("client "+channelFuture.isSuccess());
            }
        });
    }
}
