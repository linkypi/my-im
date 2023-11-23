package com.hiraeth.im.common.util;

import io.netty.channel.socket.SocketChannel;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 18:00
 */
public class CommonUtil {
    private CommonUtil(){
    }
    public static String getGatewayChannelId(SocketChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel can not be null");
        }
        return channel.remoteAddress().getHostName() + ":" + channel.remoteAddress().getPort();
    }
}
