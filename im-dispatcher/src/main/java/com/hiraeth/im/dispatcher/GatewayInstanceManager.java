package com.hiraeth.im.dispatcher;

import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接入系统实例管理组件
 * @author: lynch
 * @description:
 * @date: 2023/11/20 21:47
 */
@Component
public class GatewayInstanceManager {

    /**
     * 接入系统实例列表
     */
    private static final Map<String, SocketChannel> gatewayInstances = new ConcurrentHashMap<String, SocketChannel>();

    public void addGatewayInstance(String channelId, SocketChannel channel){
        gatewayInstances.put(channelId, channel);
    }

    public void removeGatewayInstance(String channelId){
        gatewayInstances.remove(channelId);
    }

    public SocketChannel getGatewayInstance(String channelId){
        return gatewayInstances.get(channelId);
    }
}
