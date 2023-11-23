package com.hiraeth.im.sdk;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.*;
import com.hiraeth.im.common.entity.Response;
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
        if(RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == response.getRequestType()){
            MessageSendResponseProto.MessageSendResponse messageSendResponse = MessageSendResponseProto.MessageSendResponse.parseFrom(response.getBody());
            log.info("send msg response: {}", JSON.toJSONString(messageSendResponse));
        }
//        if(RequestTypeProto.RequestType.PUSH_MESSAGE_VALUE == response.getRequestType()){
//            MessagePushResponseProto.MessagePushResponse pushMsg = MessagePushResponseProto.MessagePushResponse.parseFrom(response.getBody());
//            log.info("server push message: {}", JSON.toJSONString(pushMsg));
//        }
    }
}
