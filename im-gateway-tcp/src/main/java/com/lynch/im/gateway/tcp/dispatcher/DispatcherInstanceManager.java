package com.lynch.im.gateway.tcp.dispatcher;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 分发系统管理组件
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:44
 */
@Slf4j
public class DispatcherInstanceManager {

    static class Singleton{
        static DispatcherInstanceManager instanceManager = new DispatcherInstanceManager();
    }
    public static DispatcherInstanceManager getInstance(){
        return Singleton.instanceManager;
    }
    /**
     * 分发系统实例列表
     */
    private static final List<DispatcherInstanceAddress> dispatcherInstanceAddresses = new ArrayList<DispatcherInstanceAddress>();
    static {
        // 此处仅做演示使用，实际场景应从注册服务拉取
        dispatcherInstanceAddresses.add(new DispatcherInstanceAddress("localhost","127.0.0.1", 8090));
    }

    /**
     * 分发系统实例地址
     */
    private static final List<SocketChannel> dispatcherInstances = new ArrayList<SocketChannel>();

    public void init(){

        // 主动与一批分发系统建立长连接
        for(DispatcherInstanceAddress instance: dispatcherInstanceAddresses){
            try {
                connectDispatchInstance(instance);
            }catch (Exception ex){
                log.error("connect to dispatcher instance occur error", ex);
            }
        }
    }

    private void connectDispatchInstance(DispatcherInstanceAddress instance) throws InterruptedException {
        Bootstrap client = new Bootstrap();
        final NioEventLoopGroup threadGroup = new NioEventLoopGroup();
        client.group(threadGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new DispatcherClientHandler());
                    }
                });
        log.info("The IM client configuration is complete");
        ChannelFuture channelFuture = client.connect(instance.getIp(), instance.getPort());

        log.info("start connect to the gateway server.");
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    log.info("IM connection complete: {}", channelFuture.channel().remoteAddress());
                    dispatcherInstances.add((SocketChannel) channelFuture.channel());
                }else{
                    log.error("IM connection occur error: {}", channelFuture.channel().remoteAddress());
                    channelFuture.channel().closeFuture();
                    threadGroup.shutdownGracefully();
                }
            }
        });
        channelFuture.sync();
    }

}
