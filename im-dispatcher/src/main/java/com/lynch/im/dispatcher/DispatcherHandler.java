package com.lynch.im.dispatcher;

import com.alibaba.fastjson.JSON;
import com.lynch.im.common.Request;
import com.lynch.im.common.Response;
import com.lynch.im.protocol.AuthenticateResponseProto;
import com.lynch.im.protocol.RequestTypeProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:27
 */
@Slf4j
public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        GatewayInstanceManager.getInstance().addInstance(channel.id().asLongText(), channel);
        log.info("gateway disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        GatewayInstanceManager.getInstance().removeInstance(channel.id().asLongText());
        log.info("gateway connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = new Request((ByteBuf)msg);
        log.info("receive msg from dispatcher server: {}", JSON.toJSONString(request));
        RequestHandler instance = RequestHandler.getInstance();
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == request.getRequestType()){
            // 连接 SSO 单点登录系统进行登录认证
            Response response = instance.authenticate(request);
            ctx.writeAndFlush(response.getBuffer());
            return;
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
