package com.lynch.im.sdk;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo
 * @ClassName IMClient
 * @description: TODO
 * @date 11/20/23 3:34 PM
 */
@Slf4j
public class IMClient {
    private static final int PORT = 8080;

    private SocketChannel socketChannel;
    private Bootstrap client;
    private EventLoopGroup threadGroup;

    public void connect(String host, int port){
        try {
            this.client = new Bootstrap();
            this.threadGroup = new NioEventLoopGroup();
            client.group(threadGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new IMClientHandler());
                        }
                    });
            log.info("The IM client configuration is complete");
            ChannelFuture channelFuture = client.connect(host, PORT).sync();

            log.info("start connect to the gateway server.");
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        log.info("IM connection complete: {}", channelFuture.channel().remoteAddress());
                        socketChannel = (SocketChannel) channelFuture.channel();
                    }else{
                        channelFuture.channel().closeFuture();
                        threadGroup.shutdownGracefully();
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            log.error("start im client occur error", ex);
        }
    }

    public void authenticate(String userId, String token) {
        log.info("start authenticate...");
        sendInternal("auth|" + userId + "|" + token);
    }

    public void send(String userId, String msg){
        log.info("send one msg to gateway server.");
        sendInternal(msg + "|" + userId);
    }

    private void sendInternal(String message){
        byte[] bytes = message.getBytes();
        ByteBuf buffer = Unpooled.buffer(bytes.length);
        buffer.writeBytes(bytes);
        socketChannel.writeAndFlush(buffer);
    }

    public void close(){
       this.socketChannel.close();
       threadGroup.shutdownGracefully();
    }

}
