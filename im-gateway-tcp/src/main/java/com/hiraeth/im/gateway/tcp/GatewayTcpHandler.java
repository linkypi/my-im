package com.hiraeth.im.gateway.tcp;

import com.hiraeth.im.cache.IRedisService;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.common.entity.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.hiraeth.im.common.Constant.SESSIONS_KEY_PREFIX;


/**
 * @author leo
 * @ClassName GatewayTcpHandler
 * @description: TODO
 * @date 11/20/23 3:12 PM
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class GatewayTcpHandler extends ChannelInboundHandlerAdapter {

    private final IRedisService redisService;

    private final ResponseHandler responseHandler;

    private final RequestHandler requestHandler;

    public GatewayTcpHandler(IRedisService redisService, ResponseHandler responseHandler, RequestHandler requestHandler) {
        this.redisService = redisService;
        this.responseHandler = responseHandler;
        this.requestHandler = requestHandler;
    }

    /**
     * 客户端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        redisService.del(SESSIONS_KEY_PREFIX + SessionManager.getUserId(channel));
        SessionManager.removeSession(channel);
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

        try {
            log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

            if (MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()) {
                requestHandler.handle(baseMessage.toRequest(), (SocketChannel) ctx.channel());
                return;
            }
            if (MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()) {
                responseHandler.handle(baseMessage.toResponse(), (SocketChannel) ctx.channel());
                return;
            }
        } catch (Exception ex) {
            log.error("handle message occur error, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence(), ex);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
