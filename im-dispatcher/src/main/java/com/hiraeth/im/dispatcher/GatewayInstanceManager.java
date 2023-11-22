package com.hiraeth.im.dispatcher;

import io.netty.channel.socket.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接入系统实例管理组件
 * @author: lynch
 * @description:
 * @date: 2023/11/20 21:47
 */
public class GatewayInstanceManager {
    static class Singleton{
        private static GatewayInstanceManager instance = new GatewayInstanceManager();
    }

    public static GatewayInstanceManager getInstance(){
        return Singleton.instance;
    }
    /**
     * 接入系统实例列表
     */
    private static final Map<String, SocketChannel> gatewayInstances = new ConcurrentHashMap<String, SocketChannel>();

    public void addInstance(String channelId, SocketChannel channel){
        gatewayInstances.put(channelId, channel);
    }

    public void removeInstance(String channelId){
        gatewayInstances.remove(channelId);
    }
}
