package com.hiraeth.im.common;

import com.hiraeth.im.common.snowflake.SnowFlakeIdUtil;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.protocol.RequestTypeProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/22 10:20
 */
@Setter
@Getter
public class BaseMessage {
    protected byte headerLength;
    protected int appSDKVersion;
    protected MessageTypeEnum.MessageType messageType;
    protected byte requestType;
    protected long sequence;
    protected int bodyLength;
    protected byte[] body;

    protected ByteBuf buffer;

    public BaseMessage() {
    }

    public Request toRequest() {
        return new Request(appSDKVersion, messageType, requestType, sequence, bodyLength, body, buffer);
    }

    public Response toResponse() {
        return new Response(appSDKVersion, messageType, requestType, sequence, bodyLength, body, buffer);
    }

    public BaseMessage(ByteBuf buffer) {
        this.headerLength = buffer.readByte();
        this.appSDKVersion = buffer.readInt();
        this.messageType = MessageTypeEnum.MessageType.forNumber(buffer.readByte());
        this.requestType = buffer.readByte();
        this.sequence = buffer.readLong();
        this.bodyLength = buffer.readInt();
        this.body = new byte[buffer.readableBytes()];
        buffer.readBytes(this.body);
    }

    public BaseMessage(int appSDKVersion, MessageTypeEnum.MessageType messageType, RequestTypeProto.RequestType requestType, byte[] body) {

        this.headerLength = Constant.HEADER_LENGTH;
        this.appSDKVersion = appSDKVersion;
        this.messageType = messageType;
        this.requestType = (byte) requestType.getNumber();

        this.sequence = SnowFlakeIdUtil.getNextId();
        this.bodyLength = body.length;
        this.body = body;

        writeBuffer();
    }

    protected void writeBuffer() {
        this.buffer = Unpooled.buffer(Constant.HEADER_LENGTH + body.length + Constant.DELIMITER.length);
        buffer.writeByte(Constant.HEADER_LENGTH);
        buffer.writeInt(appSDKVersion);
        buffer.writeByte(messageType.getNumber());
        buffer.writeByte(requestType);
        buffer.writeLong(SnowFlakeIdUtil.getNextId());
        buffer.writeInt(body.length);
        buffer.writeBytes(body);
        buffer.writeBytes(Constant.DELIMITER);
    }
}
