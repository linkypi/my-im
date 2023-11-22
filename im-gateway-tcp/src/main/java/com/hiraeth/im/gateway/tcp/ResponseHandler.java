package com.hiraeth.im.gateway.tcp;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.protocol.AuthenticateResponseProto;
import com.hiraeth.im.protocol.RequestTypeProto;
import com.hiraeth.im.common.Response;
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
    public void handle(Response response) throws InvalidProtocolBufferException {
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }
    }
}
