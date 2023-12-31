package com.hiraeth.im.dispatcher.handler;

import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.dispatcher.GatewayInstanceManager;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.common.entity.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:27
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DispatcherHandler extends ChannelInboundHandlerAdapter {

    private final RequestHandler requestHandler;

    private final ResponseHandler responseHandler;

    private final GatewayInstanceManager gatewayInstanceManager;

    public DispatcherHandler(RequestHandler requestHandler, ResponseHandler responseHandler, GatewayInstanceManager gatewayInstanceManager) {
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
        this.gatewayInstanceManager = gatewayInstanceManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
        gatewayInstanceManager.addGatewayInstance(gatewayChannelId, channel);
        log.info("gateway server connected: {}", gatewayChannelId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();

        String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
        gatewayInstanceManager.removeGatewayInstance(gatewayChannelId);
        log.info("gateway server disconnected: {}", gatewayChannelId);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        BaseMessage baseMessage = new BaseMessage(buffer);

        try {
            log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

            if (MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()) {
                requestHandler.handle(baseMessage.toRequest(), (SocketChannel) ctx.channel());
            }
            if (MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()) {
                responseHandler.handle(baseMessage.toResponse(), (SocketChannel) ctx.channel());
            }
        } catch (Exception ex) {
            log.error("handle message occur error, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence(), ex);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
