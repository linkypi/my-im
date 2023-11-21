package com.lynch.im.dispatcher;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lynch.im.common.Request;
import com.lynch.im.common.Response;
import com.lynch.im.protocol.AuthenticateRequestProto.*;
import com.lynch.im.protocol.AuthenticateResponseProto.*;
import com.lynch.im.protocol.StatusCodeEnum;
import lombok.extern.java.Log;
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
        static RequestHandler instance = new RequestHandler();
    }

    public static RequestHandler getInstance(){
        return Singleton.instance;
    }

    public Response authenticate(Request request) throws InvalidProtocolBufferException {

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
