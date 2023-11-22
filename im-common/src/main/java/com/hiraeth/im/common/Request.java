package com.hiraeth.im.common;

import com.hiraeth.im.common.snowflake.SnowFlakeIdUtil;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.protocol.RequestTypeProto.RequestType;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * @author leo
 * @ClassName Request
 * @description: TODO
 * @date 11/21/23 12:38 PM
 */
@Getter
@Setter
public class Request extends BaseMessage {

    public Request(ByteBuf buffer) {
        super(buffer);
        this.messageType = MessageTypeEnum.MessageType.REQUEST;
    }

    public Request(int appSDKVersion, RequestType requestType, byte[] body) {
        super(appSDKVersion, MessageTypeEnum.MessageType.REQUEST, requestType, body);
    }

    public Request(int appSDKVersion, MessageTypeEnum.MessageType messageType,
                            byte requestType, long sequence, int bodyLength, byte[] body, ByteBuf buffer){
        this.headerLength = Constant.HEADER_LENGTH;
        this.appSDKVersion = appSDKVersion;
        this.messageType = messageType;
        this.requestType = requestType;
        this.sequence =sequence;
        this.bodyLength = bodyLength;

        this.sequence = SnowFlakeIdUtil.getNextId();
        this.bodyLength = body.length;
        this.body = body;
        this.buffer = buffer;
        writeBuffer();
    }
}
