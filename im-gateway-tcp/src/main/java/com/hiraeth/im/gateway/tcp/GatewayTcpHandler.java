package com.hiraeth.im.gateway.tcp;

import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.common.BaseMessage;
import com.hiraeth.im.common.Request;
import com.hiraeth.im.common.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author leo
 * @ClassName GatewayTcpHandler
 * @description: TODO
 * @date 11/20/23 3:12 PM
 */
@Slf4j
public class GatewayTcpHandler extends ChannelInboundHandlerAdapter {

    /**
     * 客户端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        SessionManager instance = SessionManager.getInstance();
        instance.removeSession(channel);
        log.info("app client disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("app client connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buffer = (ByteBuf) msg;
        BaseMessage baseMessage = new BaseMessage(buffer);

        log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

        if(MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()){
            RequestHandler instance = RequestHandler.getInstance();
            instance.handle(baseMessage.toRequest(), (SocketChannel) ctx.channel());
            return;
        }
        if(MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()){
            ResponseHandler instance = ResponseHandler.getInstance();
            instance.handle(baseMessage.toResponse());
            return;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
