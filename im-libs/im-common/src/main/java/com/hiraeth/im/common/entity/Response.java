package com.hiraeth.im.common.entity;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.snowflake.SnowFlakeIdUtil;
import com.hiraeth.im.protocol.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 * @ClassName Response
 * @description: TODO
 * @date 11/21/23 2:49 PM
 */
@Getter
@Setter
public class Response extends BaseMessage {

    public Response(Request request, byte[] body) {
        this.messageType = MessageTypeEnum.MessageType.RESPONSE;
        this.headerLength = request.getHeaderLength();
        this.appSDKVersion = request.getAppSDKVersion();
        this.requestType = request.getRequestType();
        this.sequence = request.getSequence();
        this.bodyLength = request.getBodyLength();
        this.body = body;

        writeBuffer();
    }

    public Response(ByteBuf buffer) {
        super(buffer);
        this.messageType = MessageTypeEnum.MessageType.RESPONSE;
    }

    public Response(int appSDKVersion, MessageTypeEnum.MessageType messageType,
                   byte requestType, long sequence, int bodyLength, byte[] body){
        this.headerLength = Constant.HEADER_LENGTH;
        this.appSDKVersion = appSDKVersion;
        this.messageType = messageType;
        this.requestType = requestType;
        this.sequence =sequence;
        this.bodyLength = bodyLength;

        this.sequence = SnowFlakeIdUtil.getNextId();
        this.bodyLength = body.length;
        this.body = body;
        writeBuffer();
    }
}
