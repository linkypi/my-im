package com.lynch.im.gateway.tcp;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

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
    private int sequence;
    private int bodyLength;
    private byte[] body;

    public Request(ByteBuf buffer){
        this.headerLength = buffer.readInt();
        this.appSDKVersion = buffer.readInt();
        this.requestType = buffer.readInt();
        this.sequence = buffer.readInt();
        this.bodyLength = buffer.readInt();
        this.body = new byte[buffer.readableBytes()];
        buffer.readBytes(this.body);
    }
}
