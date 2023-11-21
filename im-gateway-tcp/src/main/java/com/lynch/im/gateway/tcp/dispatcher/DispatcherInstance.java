package com.lynch.im.gateway.tcp.dispatcher;

import com.lynch.im.common.Request;
import com.lynch.im.protocol.AuthenticateRequestProto.*;
import com.lynch.im.protocol.AuthenticateResponseProto.*;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;

/**
 * 分发系统实例
 * @author: lynch
 * @description:
 * @date: 2023/11/21 21:55
 */
@Getter
@Setter
public class DispatcherInstance {
    private SocketChannel socketChannel;

    public DispatcherInstance(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 向分发系统发送认证请求
     * @param request
     * @return
     */
    public void authenticate(Request request) {
        this.socketChannel.writeAndFlush(request.getBuffer());
    }
}
