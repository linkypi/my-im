package com.lynch.im.sdk;

import com.alibaba.fastjson.JSON;
import com.lynch.im.common.Response;
import com.lynch.im.protocol.AuthenticateResponseProto.*;
import com.lynch.im.protocol.RequestTypeProto.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo
 * @ClassName IMClientHandler
 * @description: TODO
 * @date 11/20/23 3:38 PM
 */
@Slf4j
public class IMClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf)msg;
        Response response = new Response(buffer);

        log.info("receive msg from gateway server: {}", JSON.toJSONString(response));

        if(RequestType.AUTHENTICATE_VALUE == response.getRequestType()){
            AuthenticateResponse authenticateResponse = AuthenticateResponse.parseFrom(response.getBody());
            log.info("authenticate response: {}", JSON.toJSONString(authenticateResponse));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("client occur error: {}", ctx.channel().remoteAddress(), cause);
    }
}
