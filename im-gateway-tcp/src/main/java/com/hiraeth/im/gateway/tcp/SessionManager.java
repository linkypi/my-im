package com.hiraeth.im.gateway.tcp;

import com.hiraeth.im.common.util.CommonUtil;
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

    /**
     * 保存客户端连接
     */
    private static final Map<String, SocketChannel> sessions = new ConcurrentHashMap<String, SocketChannel>();
    /**
     * 保存客户端 id
     */
    private static final Map<String, String> channelId2Uid = new ConcurrentHashMap<String, String>();

    public static void addSession(String userId, SocketChannel socketChannel){
        sessions.put(userId, socketChannel);

        String key = CommonUtil.getGatewayChannelId(socketChannel);
        channelId2Uid.put(key, userId);
    }

    public static boolean isConnected(String userId){
        return sessions.containsKey(userId);
    }

    public static SocketChannel getSession(String userId){
        return sessions.get(userId);
    }

    /**
     * 删除客户端连接
     * @param channel
     */
    public static void removeSession(SocketChannel channel){
        String key = CommonUtil.getGatewayChannelId(channel);
        String userId = channelId2Uid.get(key);
        sessions.remove(userId);
        channelId2Uid.remove(key);
    }

    public static String getUserId(SocketChannel channel){
        String key = CommonUtil.getGatewayChannelId(channel);
        return channelId2Uid.get(key);
    }

}
