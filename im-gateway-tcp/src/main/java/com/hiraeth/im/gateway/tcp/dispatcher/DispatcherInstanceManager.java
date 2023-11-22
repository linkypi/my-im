package com.hiraeth.im.gateway.tcp.dispatcher;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.hiraeth.im.common.Constant.DELIMITER;

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
    private static final List<DispatcherInstanceAddress> dispatcherInstanceAddresses = new ArrayList<>();
    static {
        // 此处仅做演示使用，实际场景应从注册服务拉取
        dispatcherInstanceAddresses.add(new DispatcherInstanceAddress("localhost","127.0.0.1", 8090));
    }

    private static final Random random = new Random();;
    public DispatcherInstance chooseDispatcherInstance(){
        List<DispatcherInstance> list = new ArrayList<>(dispatcherInstances.size());
        list.addAll(dispatcherInstances.values());
        int index = random.nextInt(dispatcherInstances.size());
        return list.get(index);
    }

    public void removeDistanceInstance(String gatewayChannelId){
        dispatcherInstances.remove(gatewayChannelId);
    }
    /**
     * 分发系统实例地址
     */
    private static final Map<String, DispatcherInstance> dispatcherInstances = new ConcurrentHashMap<>();

    public void init(){
        // 主动与一批分发系统实例建立长连接
        for(DispatcherInstanceAddress instance: dispatcherInstanceAddresses){
            try {
                connectDispatchInstance(instance);
            }catch (Exception ex){
                log.error("connect to dispatcher instance occur error, instance: {}", JSON.toJSONString(instance), ex);
            }
        }
    }

    private void connectDispatchInstance(final DispatcherInstanceAddress instance) throws InterruptedException {
        Bootstrap client = new Bootstrap();
        final NioEventLoopGroup threadGroup = new NioEventLoopGroup();
        client.group(threadGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER);
                        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                        socketChannel.pipeline().addLast(new DispatcherInstanceHandler());
                    }
                });
        ChannelFuture channelFuture = client.connect(instance.getIp(), instance.getPort());

        log.info("start connect to the dispatcher instance: {} ...", JSON.toJSONString(instance));
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("connect dispatcher instance success: {}", JSON.toJSONString(instance));
                    SocketChannel channel = (SocketChannel) channelFuture.channel();
                    DispatcherInstance dispatcherInstance = new DispatcherInstance(channel);
                    String gatewayChannelId = channel.remoteAddress().getHostName() + ":" + channel.remoteAddress().getPort();
                    dispatcherInstances.put(gatewayChannelId, dispatcherInstance);
                } else {
                    log.error("connect dispatcher instance occur error: {}", JSON.toJSONString(instance));
                    channelFuture.channel().closeFuture();
                    threadGroup.shutdownGracefully();
                }
            }
        });
        channelFuture.sync();
    }

}