package com.hiraeth.im.gateway.tcp;


import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.AuthenticateRequestProto;
import com.hiraeth.im.protocol.MessageSendRequestProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.gateway.tcp.dispatcher.DispatcherInstance;
import com.hiraeth.im.gateway.tcp.dispatcher.DispatcherInstanceManager;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author leo
 * @ClassName RequestHandler
 * @description: TODO
 * @date 11/21/23 2:07 PM
 */
@Slf4j
@Component
public class RequestHandler {

    /**
     * 将 APP 端的请求转发到dispatcher系统进行认证, 认证通过后记录session
     * @param request
     * @return
     */
    public void authenticate(Request request) {
        // 随机选择一个Dispatcher分发系统地址进行请求, 随后分发系统接收到该请求后即可连接 SSO 单点登录系统进行认证
        DispatcherInstance instance = DispatcherInstanceManager.chooseDispatcherInstance();
        instance.authenticate(request);
    }

    public void sendMessage(Request request) {
        // 随机选择一个Dispatcher分发系统地址进行转发
        DispatcherInstance instance = DispatcherInstanceManager.chooseDispatcherInstance();
        instance.sendMessage(request);
    }

    public void handle(Request request, SocketChannel socketChannel) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()){
            authenticate(request);
            // 记录用户会话，以便在请求异步返回后可以使用该会话对请求进行回复
            AuthenticateRequestProto.AuthenticateRequest authenticateRequest = AuthenticateRequestProto.AuthenticateRequest.parseFrom(request.getBody());
            SessionManager.addSession(authenticateRequest.getUid(), socketChannel);
            return;
        }

        if(RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == request.getRequestType()){
            MessageSendRequestProto.MessageSendRequest msg = MessageSendRequestProto.MessageSendRequest.parseFrom(request.getBody());
            log.info("forward message to dispatcher server: {}", JSON.toJSONString(msg));
            sendMessage(request);
            return;
        }

        log.warn("unknown request: {}", JSON.toJSONString(request));
    }
}
