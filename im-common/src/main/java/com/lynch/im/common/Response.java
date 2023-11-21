package com.lynch.im.common;

import com.lynch.im.protocol.RequestTypeProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class Response {
    private int headerLength;
    private int appSDKVersion;
    private int requestType;
    private long sequence;
    private int bodyLength;
    private byte[] body;

    private ByteBuf buffer;

    private static final int HEADER_LENGTH = 23;
    private static final int APP_SDK_VERSION = 1;
    private static final int SEQUENCE = 1;
    private static final byte[] DELIMITER = "$_".getBytes();

    public Response(Request request, byte[] body){
        this.headerLength = request.getHeaderLength();
        this.appSDKVersion = request.getAppSDKVersion();
        this.requestType = request.getRequestType();
        this.sequence = request.getSequence();
        this.bodyLength = request.getBodyLength();
        this.body = body;

        ByteBuf buffer = Unpooled.buffer(HEADER_LENGTH +  body.length + DELIMITER.length);
        buffer.writeInt(HEADER_LENGTH);
        buffer.writeInt(APP_SDK_VERSION);
        buffer.writeInt(RequestTypeProto.RequestType.AUTHENTICATE_VALUE);
        buffer.writeLong(SEQUENCE);
        buffer.writeInt(body.length);
        buffer.writeBytes(body);
        buffer.writeBytes(DELIMITER);
    }

    public Response(ByteBuf buffer){
        this.headerLength = buffer.readInt();
        this.appSDKVersion = buffer.readInt();
        this.requestType = buffer.readInt();
        this.sequence = buffer.readLong();
        this.bodyLength = buffer.readInt();
        this.body = new byte[buffer.readableBytes()];
        buffer.readBytes(this.body);
    }
}
