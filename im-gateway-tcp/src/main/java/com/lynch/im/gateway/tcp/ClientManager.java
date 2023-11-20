package com.lynch.im.gateway.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理客户端长连接的组件
 * @author leo
 * @ClassName ClientManager
 * @description: TODO
 * @date 11/20/23 4:02 PM
 */
public class ClientManager {
    public ClientManager(){

    }
    static class Singleton{
        public static volatile ClientManager instance = new ClientManager();
    }
    public static ClientManager getInstance(){
        return Singleton.instance;
    }

    /**
     * 保存客户端连接
     */
    private static final Map<String, SocketChannel> uid2Clients = new ConcurrentHashMap<String, SocketChannel>();
    /**
     * 保存客户端 id
     */
    private static final Map<String, String> channelId2Uid = new ConcurrentHashMap<String, String>();

    public void addClient(String userId, SocketChannel socketChannel){
        uid2Clients.put(userId, socketChannel);
        channelId2Uid.put(socketChannel.remoteAddress().getHostName(), userId);
    }

    public boolean isClientConnected(String userId){
        return uid2Clients.containsKey(userId);
    }

    public SocketChannel getClient(String userId){
        return uid2Clients.get(userId);
    }

    /**
     * 删除客户端连接
     * @param channel
     */
    public void removeClient(SocketChannel channel){
        String hostName = channel.remoteAddress().getHostName();
        String userId = channelId2Uid.get(hostName);
        uid2Clients.remove(userId);
        channelId2Uid.remove(hostName);
    }
}
