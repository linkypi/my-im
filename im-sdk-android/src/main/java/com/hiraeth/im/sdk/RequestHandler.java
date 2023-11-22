package com.hiraeth.im.sdk;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.AuthenticateResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.Request;
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

    public void handle(Request request) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(request.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }
    }
}
