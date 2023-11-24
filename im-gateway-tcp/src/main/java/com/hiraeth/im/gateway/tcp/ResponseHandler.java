package com.hiraeth.im.gateway.tcp;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.gateway.tcp.dispatcher.DispatcherInstanceManager;
import com.hiraeth.im.protocol.AuthenticateResponseProto.*;
import com.hiraeth.im.protocol.MessageForwardResponseProto;
import com.hiraeth.im.protocol.MessagePushResponseProto.*;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.protocol.RequestTypeProto.*;
import com.hiraeth.im.common.entity.Response;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: leo
 * @description:
 * @ClassName: com.lynch.im.sdk
 * @date: 2023/11/22 11:09
 */
@Slf4j
@Component
public class ResponseHandler {
    public void handle(Response response, SocketChannel channel) throws InvalidProtocolBufferException {
        if (RequestType.AUTHENTICATE_VALUE == response.getRequestType()) {
            AuthenticateResponse authenticateResponse = AuthenticateResponse.parseFrom(response.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }
        if (RequestType.PUSH_MESSAGE_VALUE == response.getRequestType()) {
            MessagePushResponse res = MessagePushResponse.parseFrom(response.getBody());
            log.info("push message response: {}", JSON.toJSONString(res));
        }
        if (RequestType.FORWARD_MESSAGE_VALUE == response.getRequestType()) {
            MessageForwardResponseProto.MessageForwardResponse res = MessageForwardResponseProto.MessageForwardResponse.parseFrom(response.getBody());
            log.info("forward message response: {}", JSON.toJSONString(res));
        }
        // 接收者收到消息后,将响应原路返回: APP -> gateway server -> dispatcher
        if (RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE == response.getRequestType()) {
            forwardDispatcherResponse(response, channel);
            return;
        }
    }

    private void forwardDispatcherResponse(Response response, SocketChannel channel) throws InvalidProtocolBufferException {
        MessageForwardResponseProto.MessageForwardResponse forwardResponse = MessageForwardResponseProto.MessageForwardResponse.parseFrom(response.getBody());
        SocketChannel dispatcherChannel = DispatcherInstanceManager.getDispatcherChannel(forwardResponse.getReceiverId());
        if(dispatcherChannel == null){
            log.error("forward response to dispatcher failed, because channel not found, response: {}", JSON.toJSONString(forwardResponse));
            return;
        }
        dispatcherChannel.writeAndFlush(response.getBuffer());
        DispatcherInstanceManager.removeDispatcherChannel(forwardResponse.getReceiverId());
    }
}
