package com.lynch.im.gateway.tcp;


import com.google.protobuf.InvalidProtocolBufferException;
import com.lynch.im.common.Constant;
import com.lynch.im.common.Request;
import com.lynch.im.gateway.tcp.dispatcher.DispatcherInstance;
import com.lynch.im.gateway.tcp.dispatcher.DispatcherInstanceManager;
import com.lynch.im.protocol.AuthenticateRequestProto.*;
import com.lynch.im.protocol.AuthenticateResponseProto.*;
import com.lynch.im.protocol.RequestTypeProto;
import io.netty.channel.socket.SocketChannel;

/**
 * @author leo
 * @ClassName RequestHandler
 * @description: TODO
 * @date 11/21/23 2:07 PM
 */
public class RequestHandler {
    private RequestHandler(){
    }

    static class Singleton{
        static RequestHandler instance = new RequestHandler();
    }

    public static RequestHandler getInstance(){
        return Singleton.instance;
    }

    /**
     * 将 APP 端的请求转发到dispatcher系统进行认证, 认证通过后记录session
     * @param request
     * @return
     */
    public void authenticate(Request request) {
        // 随机选择一个Dispatcher分发系统地址进行请求, 随后分发系统接收到该请求后即可连接 SSO 单点登录系统进行认证
        DispatcherInstanceManager dispatcherInstanceManager = DispatcherInstanceManager.getInstance();
        DispatcherInstance instance = dispatcherInstanceManager.chooseDispatcherInstance();
        instance.authenticate(request);
    }
}
