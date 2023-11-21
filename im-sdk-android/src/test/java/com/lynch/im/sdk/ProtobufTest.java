package com.lynch.im.sdk;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lynch.im.protocol.AuthenticateRequestProto;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo
 * @ClassName ProtobufTest
 * @description: TODO
 * @date 11/21/23 11:04 AM
 */
@Slf4j
public class ProtobufTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        AuthenticateRequestProto.AuthenticateRequest request = createRequest();
        byte[] bytes = request.toByteArray();
        log.info("request length: {}", bytes.length);

        AuthenticateRequestProto.AuthenticateRequest authenticateRequest = AuthenticateRequestProto.AuthenticateRequest.parseFrom(bytes);
        log.info("deserialize object: {}", JSON.toJSONString(authenticateRequest));

    }

    private static AuthenticateRequestProto.AuthenticateRequest createRequest(){
        AuthenticateRequestProto.AuthenticateRequest.Builder builder = AuthenticateRequestProto.AuthenticateRequest.newBuilder();
        builder.setUid("test001");
        builder.setToken("token");
        builder.setTimestamp(System.currentTimeMillis());
        return builder.build();
    }
}
