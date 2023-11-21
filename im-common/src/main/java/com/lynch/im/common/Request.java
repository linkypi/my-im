package com.lynch.im.common;

import com.lynch.im.common.snowflake.SnowFlakeIdUtil;
import com.lynch.im.protocol.RequestTypeProto.RequestType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

import static com.lynch.im.common.Constant.*;

/**
 * @author leo
 * @ClassName Requesrt
 * @description: TODO
 * @date 11/21/23 12:38 PM
 */
@Getter
@Setter
public class Request {
    private int headerLength;
    private int appSDKVersion;
    private int requestType;
    private long sequence;
    private int bodyLength;
    private byte[] body;

    private ByteBuf buffer;

    public Request(ByteBuf buffer){
        this.headerLength = buffer.readInt();
        this.appSDKVersion = buffer.readInt();
        this.requestType = buffer.readInt();
        this.sequence = buffer.readLong();
        this.bodyLength = buffer.readInt();
        this.body = new byte[buffer.readableBytes()];
        buffer.readBytes(this.body);
    }

    public Request(int appSDKVersion, RequestType requestType, byte[] body){

        long sequenceId = SnowFlakeIdUtil.getNextId();
        this.headerLength = HEADER_LENGTH;
        this.appSDKVersion = appSDKVersion;
        this.requestType = requestType.getNumber();
        this.sequence = sequenceId;
        this.bodyLength = body.length;
        this.body = body;

        this.buffer = Unpooled.buffer(HEADER_LENGTH +  body.length + DELIMITER.length);
        buffer.writeInt(HEADER_LENGTH);
        buffer.writeInt(appSDKVersion);
        buffer.writeInt(requestType.getNumber());
        buffer.writeLong(sequenceId);
        buffer.writeInt(body.length);
        buffer.writeBytes(body);
        buffer.writeBytes(DELIMITER);
    }
}
