package com.lynch.im.dispatcher;

import com.lynch.im.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:02
 */
@Slf4j
public class DispatcherServer {

    private static final int PORT = 8090;
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup ioEventLoopGroup = new NioEventLoopGroup();

        try {
            log.info("start dispatcher server ... ");
            ServerBootstrap server = new ServerBootstrap();
            server.group(eventLoopGroup, ioEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER);
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                            socketChannel.pipeline().addLast(new DispatcherHandler());
                        }
                    });
            ChannelFuture channelFuture = server.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            log.error("start dispatcher server occur error", ex);
        }finally {
            eventLoopGroup.shutdownGracefully();
            ioEventLoopGroup.shutdownGracefully();
        }
    }

}
