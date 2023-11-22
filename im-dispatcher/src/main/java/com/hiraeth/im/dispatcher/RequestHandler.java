package com.hiraeth.im.dispatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.Request;
import com.hiraeth.im.common.Response;
import com.hiraeth.im.protocol.AuthenticateRequestProto.*;
import com.hiraeth.im.protocol.AuthenticateResponseProto.*;
import com.hiraeth.im.protocol.StatusCodeEnum;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/21 22:10
 */
@Slf4j
public class RequestHandler {
    private RequestHandler(){
    }
    static class Singleton{
        static final RequestHandler instance = new RequestHandler();
    }

    public static RequestHandler getInstance(){
        return Singleton.instance;
    }

    public void handle(Request request, SocketChannel socketChannel) throws InvalidProtocolBufferException {
        if (RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()) {
            Response response = authenticate(request, socketChannel);
            socketChannel.writeAndFlush(response.getBuffer());
            return;
        }
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
                JedisManager jedisManager = JedisManager.getInstance();
                jedisManager.getJedis().set("sessions:"+ userId, jsonObject.toJSONString());

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
