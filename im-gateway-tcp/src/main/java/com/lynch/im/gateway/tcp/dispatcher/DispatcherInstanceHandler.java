package com.lynch.im.gateway.tcp.dispatcher;

import com.alibaba.fastjson.JSON;
import com.lynch.im.common.Response;
import com.lynch.im.gateway.tcp.SessionManager;
import com.lynch.im.protocol.AuthenticateRequestProto;
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
 * @date: 2023/11/20 23:23
 */
@Slf4j
public class DispatcherInstanceHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        log.info("gateway disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        log.info("gateway connected: {}", ctx.channel().remoteAddress());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = new Response((ByteBuf)msg);
        log.info("receive msg from dispatcher server: {}", JSON.toJSONString(response));
        if(RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()){
            AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());

            // 将响应转发到此前请求过来的 App 会话
            SessionManager sessionManager = SessionManager.getInstance();
            SocketChannel session = sessionManager.getSession(authenticateResponse.getUid());
            session.writeAndFlush(response.getBuffer());

            // 若认证通过则需设置本地session以及Redis中的分布式session
            if(authenticateResponse.getSuccess()) {
                log.info("authenticate success, userId: {}", authenticateResponse.getUid());
                SessionManager instance = SessionManager.getInstance();
                instance.addSession(authenticateResponse.getUid(), (SocketChannel) ctx.channel());
                return;
            }
            log.info("authenticate failed, response: {}", JSON.toJSONString(authenticateResponse));
        }
        ctx.writeAndFlush(response.getBuffer());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("client occur error: {}", ctx.channel().remoteAddress(), cause);
    }

}
