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
public class SessionManager {
    public SessionManager(){

    }
    static class Singleton{
        public static volatile SessionManager instance = new SessionManager();
    }
    public static SessionManager getInstance(){
        return Singleton.instance;
    }

    /**
     * 保存客户端连接
     */
    private static final Map<String, SocketChannel> sessions = new ConcurrentHashMap<String, SocketChannel>();
    /**
     * 保存客户端 id
     */
    private static final Map<String, String> channelId2Uid = new ConcurrentHashMap<String, String>();

    public void addSession(String userId, SocketChannel socketChannel){
        sessions.put(userId, socketChannel);
        channelId2Uid.put(socketChannel.remoteAddress().getHostName(), userId);
    }

    public boolean isConnected(String userId){
        return sessions.containsKey(userId);
    }

    public SocketChannel getSession(String userId){
        return sessions.get(userId);
    }

    /**
     * 删除客户端连接
     * @param channel
     */
    public void removeSession(SocketChannel channel){
        String hostName = channel.remoteAddress().getHostName();
        String userId = channelId2Uid.get(hostName);
        sessions.remove(userId);
        channelId2Uid.remove(hostName);
    }
}
