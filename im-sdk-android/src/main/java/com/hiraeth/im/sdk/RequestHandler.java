package com.hiraeth.im.sdk;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.common.entity.Response;
import com.hiraeth.im.protocol.*;
import com.hiraeth.im.common.entity.Request;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author:
 * @description:
 * @ClassName: com.lynch.im.sdk
 * @date: 2023/11/22 11:08
 */
@Slf4j
public class RequestHandler {
    private RequestHandler(){
    }

    static class Singleton{
        private static final RequestHandler instance = new RequestHandler();
    }

    public static RequestHandler getInstance(){
        return Singleton.instance;
    }

    public void handle(Request request, SocketChannel channel) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(request.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }

        if(RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE == request.getRequestType()){
            MessageForwardRequestProto.MessageForwardRequest forwardRequest = MessageForwardRequestProto.MessageForwardRequest.parseFrom(request.getBody());
            log.info("forward message: {}", JSON.toJSONString(forwardRequest));

            // 回应服务器已接收到转发的信息
            MessageForwardResponseProto.MessageForwardResponse.Builder builder = MessageForwardResponseProto.MessageForwardResponse.newBuilder();
            builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
            builder.setSuccess(true);
            builder.setReceiverId(forwardRequest.getReceiverId());
            builder.setMessageId(forwardRequest.getMessageId());
            Response response = new Response(request, builder.build().toByteArray());
            channel.writeAndFlush(response.getBuffer());
        }

        if(RequestTypeProto.RequestType.PUSH_MESSAGE_VALUE == request.getRequestType()){
            MessagePushRequestProto.MessagePushRequest pushMsg = MessagePushRequestProto.MessagePushRequest.parseFrom(request.getBody());
            log.info("server push message: {}", JSON.toJSONString(pushMsg));

            // 回应服务器已接收到推送信息
            MessagePushResponseProto.MessagePushResponse.Builder builder = MessagePushResponseProto.MessagePushResponse.newBuilder();
            builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
            builder.setSuccess(true);
            Response response = new Response(request, builder.build().toByteArray());
            channel.writeAndFlush(response.getBuffer());
        }
    }
}
