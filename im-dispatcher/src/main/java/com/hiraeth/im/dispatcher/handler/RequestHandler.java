package com.hiraeth.im.dispatcher.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.cache.IRedisService;
//import com.hiraeth.im.dispatcher.JedisManager;
import com.hiraeth.im.protocol.MessageProto;
import com.hiraeth.im.protocol.MessageResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.Request;
import com.hiraeth.im.common.Response;
import com.hiraeth.im.protocol.AuthenticateRequestProto.*;
import com.hiraeth.im.protocol.AuthenticateResponseProto.*;
import com.hiraeth.im.protocol.StatusCodeEnum;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/21 22:10
 */
@Slf4j
@Component
public class RequestHandler {
//    private RequestHandler(){
//    }
//    static class Singleton{
//        static final RequestHandler instance = new RequestHandler();
//    }
//
//    public static RequestHandler getInstance(){
//        return Singleton.instance;
//    }

    @Autowired
    private IRedisService redisService;

    public void handle(Request request, SocketChannel socketChannel) throws InvalidProtocolBufferException {
        if (RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()) {
            Response response = authenticate(request, socketChannel);
            socketChannel.writeAndFlush(response.getBuffer());
            return;
        }
        if (RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == request.getRequestType()) {
            Response response = saveMessage(request);
            socketChannel.writeAndFlush(response.getBuffer());
            return;
        }
        log.warn("unknown request: {}", JSON.toJSONString(request));
    }

    public Response saveMessage(Request request) throws InvalidProtocolBufferException {
        MessageProto.Message msg = MessageProto.Message.parseFrom(request.getBody());
        log.info("get message from gateway server: {}", JSON.toJSONString(msg));

        MessageResponseProto.MessageResponse.Builder builder = MessageResponseProto.MessageResponse.newBuilder();
        builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
        builder.setSuccess(true);
        builder.setUid(msg.getFromUid());
        builder.setTimestamp(System.currentTimeMillis());
        return new Response(request, builder.build().toByteArray());
    }

    public Response authenticate(Request request, SocketChannel channel) throws InvalidProtocolBufferException {

        AuthenticateRequest authenticateRequest = AuthenticateRequest.parseFrom(request.getBody());
        String userId = authenticateRequest.getUid();
        String token = authenticateRequest.getToken();

        AuthenticateResponse.Builder builder = AuthenticateResponse.newBuilder();
        builder.setUid(userId);
        builder.setToken(token);
        builder.setTimestamp(System.currentTimeMillis());

        try {
            // 连接 SSO 单点登录系统验证 userId 及 token 是否有效
            if ("test001".equals(userId) && "token".equals(token)) {

                // 将 session 存储到 Redis 中
                // key = uid , value = {
                //    'isAuthenticated':true,
                //    'token': '...',
                //    'timestamp': 'xxx',
                //    'authenticatedTime': '...',
                //    'gatewayChannelId': 564
                //   }
                String gatewayChannelId = channel.remoteAddress().getHostName() + ":"
                + channel.remoteAddress().getPort();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                jsonObject.put("timestamp", System.currentTimeMillis());
                jsonObject.put("authenticatedTime", System.currentTimeMillis());
                jsonObject.put("isAuthenticated", true);
                jsonObject.put("gatewayChannelId", gatewayChannelId);
                redisService.set("sessions:"+ userId, jsonObject.toJSONString());

                builder.setSuccess(true);
                builder.setCode(StatusCodeEnum.StatusCode.SUCCESS);
                builder.setMessage("");
                return new Response(request, builder.build().toByteArray());
            }
        }catch (Exception ex){
            log.error("authenticate occur error, request: {}", JSON.toJSONString(request), ex);
        }
        builder.setSuccess(false);
        builder.setCode(StatusCodeEnum.StatusCode.UNAUTHORIZED);
        builder.setMessage("user or password is invalid");
        return new Response(request, builder.build().toByteArray());
    }
}
