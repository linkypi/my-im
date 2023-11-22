package com.hiraeth.im.gateway.tcp.dispatcher;

import com.alibaba.fastjson.JSON;
import com.hiraeth.im.common.BaseMessage;
import com.hiraeth.im.common.Response;
import com.hiraeth.im.gateway.tcp.SessionManager;
import com.hiraeth.im.protocol.AuthenticateResponseProto;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.protocol.RequestTypeProto;
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
        String gatewayChannelId = channel.remoteAddress().getHostName() + ":" + channel.remoteAddress().getPort();
        DispatcherInstanceManager instance = DispatcherInstanceManager.getInstance();
        instance.removeDistanceInstance(gatewayChannelId);
        log.info("gateway disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        log.info("gateway connected: {}", ctx.channel().remoteAddress());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        BaseMessage baseMessage = new BaseMessage(buffer);

        log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

        if (MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()) {

            return;
        }

        if (MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()) {
            Response response = new Response((ByteBuf) msg);
            log.info("receive msg from dispatcher server: {}", JSON.toJSONString(response));
            if (RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()) {
                AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());

                // 将响应转发到此前请求过来的 App 会话
                SessionManager sessionManager = SessionManager.getInstance();
                SocketChannel session = sessionManager.getSession(authenticateResponse.getUid());
                session.writeAndFlush(response.getBuffer());

                // 若认证通过则需设置本地session以及Redis中的分布式session
                if (authenticateResponse.getSuccess()) {
                    log.info("authenticate success, userId: {}", authenticateResponse.getUid());
                    SocketChannel channel = (SocketChannel) ctx.channel();
                    String gatewayChannelId = channel.remoteAddress().getHostName() + ":" + channel.id().asLongText();

                    // 将 session 存储到 Redis 中
                    // key = uid , value = {
                    //    'isAuthenticated':true,
                    //    'token': '...',
                    //    'timestamp': 'xxx',
                    //    'authenticatedTime': '...',
                    //    'gatewayChannelId': 564
                    //   }
                    return;
                }
                log.info("authenticate failed, response: {}", JSON.toJSONString(authenticateResponse));
            }
            ctx.writeAndFlush(response.getBuffer());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("client occur error: {}", ctx.channel().remoteAddress(), cause);
    }

}
