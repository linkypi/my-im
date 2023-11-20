package com.lynch.im.gateway.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理长连接的组件
 * @author leo
 * @ClassName NettyChanneManager
 * @description: TODO
 * @date 11/20/23 4:02 PM
 */
public class NettyChannelManager {
    public NettyChannelManager(){

    }
    static class Singleton{
        public static volatile NettyChannelManager instance = new NettyChannelManager();
    }
    public static NettyChannelManager getInstance(){
        return Singleton.instance;
    }

    /**
     * 保存客户端连接
     */
    private static final Map<String, SocketChannel> channels = new ConcurrentHashMap<String, SocketChannel>();
    /**
     * 保存客户端 id
     */
    private static final Map<String, String> channelIds = new ConcurrentHashMap<String, String>();

    public void addChannel(String userId, SocketChannel socketChannel){
        channels.put(userId, socketChannel);
        channelIds.put(socketChannel.remoteAddress().getHostName(), userId);
    }

    public boolean existChannel(String userId){
        return channels.containsKey(userId);
    }

    public SocketChannel getChannel(String userId){
        return channels.get(userId);
    }

    /**
     * 删除客户端连接
     * @param channel
     */
    public void remove(SocketChannel channel){
        String hostName = channel.remoteAddress().getHostName();
        String userId = channelIds.get(hostName);
        channels.remove(userId);
        channelIds.remove(hostName);
    }
}
