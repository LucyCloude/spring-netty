package com.eaphone.jiankang.config;

import com.eaphone.jiankang.handle.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    @Autowired
    private DiscardServerHandler discardServerHandler;

    public void run(int port) throws InterruptedException {
        //第一个线程组是用于接收Client端的连接
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //第二个线程组是用于实际业务的处理
        NioEventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, work)//绑定两个线程池
                    .channel(NioServerSocketChannel.class)//指定NIO模式,如果是客户端就是NioClientSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)//TCP的缓冲区设置
                    .option(ChannelOption.SO_SNDBUF, 50 * 1024)//设置发送缓冲区的大小
                    .option(ChannelOption.SO_RCVBUF, 50 * 1024)//设置接收缓冲区的大小
                    .option(ChannelOption.SO_BACKLOG, 128)//如果当前服务器处理请求满时，用来临时存放完成三次握手的最大长度
                    .option(ChannelOption.SO_KEEPALIVE, true)//保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());//拆包粘包定义结束字符串
                            socketChannel.pipeline()
                                   // .addLast(new DelimiterBasedFrameDecoder(1024,byteBuf))//发送的数据为$_结尾代表结束
                                    .addLast(new StringDecoder())//接收的数据转化为String 但发送的数据还是ByteBuf
                                    .addLast(discardServerHandler);

                        }
                    });
            ChannelFuture sync = serverBootstrap.bind(port).sync();//绑定端口,同步等待成功
            sync.channel().closeFuture().sync();
        } finally {
            //退出释放线程资源
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
