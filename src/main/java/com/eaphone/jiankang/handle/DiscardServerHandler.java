package com.eaphone.jiankang.handle;

import com.eaphone.jiankang.service.NettyService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * netty业务处理入口
 */
@Component
@ChannelHandler.Sharable
public class DiscardServerHandler extends ChannelHandlerAdapter {
    @Autowired
    private NettyService nettyService;

    public static final ConcurrentHashMap<String,ChannelHandlerContext> cmap=new ConcurrentHashMap<String,ChannelHandlerContext>(16);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        nettyService.service(ctx,msg);
    }
    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive... id:"+ctx.channel().id().asLongText()+"建立连接时：" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        ctx.fireChannelActive();
    }

    /**
     *  连接注册成功触发
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        cmap.put(ctx.channel().id().asLongText(),ctx);
        System.out.println("channelRegistered ctx:"+ctx+" 时间:" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));

    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive.. ctx:"+ctx+"关闭连接时：" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        ChannelHandlerContext ctxOffline = cmap.remove(ctx.channel().id().asLongText());
        if (ctxOffline!=null){
            System.out.println(ctxOffline.channel().id().asLongText()+" 服务器成功移除...");
        }
        super.channelInactive(ctx);
    }

    /**
     *client把服务强制关掉（如停止服务或杀进程），server端会异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        try {

            super.exceptionCaught(ctx, cause);
        }catch (Exception e){
            System.out.println("客户端强迫关闭一个连接 :"+cause.getMessage());
        }
    }
}
