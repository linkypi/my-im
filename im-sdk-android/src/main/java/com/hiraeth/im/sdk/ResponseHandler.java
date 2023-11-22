package com.hiraeth.im.sdk;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.AuthenticateResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: leo
 * @description:
 * @ClassName: com.lynch.im.sdk
 * @date: 2023/11/22 11:09
 */
@Slf4j
public class ResponseHandler {
    private ResponseHandler(){
    }

    static class Singleton{
        private static final ResponseHandler instance = new ResponseHandler();
    }

    public static ResponseHandler getInstance(){
        return ResponseHandler.Singleton.instance;
    }

    public void handle(Response response) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }
    }
}
