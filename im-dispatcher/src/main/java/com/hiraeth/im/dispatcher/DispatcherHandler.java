package com.hiraeth.im.dispatcher;

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
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:27
 */
@Slf4j
public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        String id = channel.remoteAddress().getHostName() + channel.remoteAddress().getPort();
        GatewayInstanceManager.getInstance().addInstance(id, channel);
        log.info("gateway server disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        String id = channel.remoteAddress().getHostName() + channel.remoteAddress().getPort();
        GatewayInstanceManager.getInstance().removeInstance(id);
        log.info("gateway server connected: {}", ctx.channel().remoteAddress());
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
        }
        if(MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()){
            ResponseHandler instance = ResponseHandler.getInstance();
            instance.handle(baseMessage.toResponse());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
