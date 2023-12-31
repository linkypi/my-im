package com.hiraeth.im.sdk;

import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.protocol.MessageTypeEnum;
import com.hiraeth.im.common.entity.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leo
 * @ClassName IMClientHandler
 * @description: TODO
 * @date 11/20/23 3:38 PM
 */
@Slf4j
public class IMClientHandler extends ChannelInboundHandlerAdapter {

    private IMClient imClient;

    public IMClientHandler(IMClient imClient){
        this.imClient = imClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buffer = (ByteBuf) msg;
            BaseMessage baseMessage = new BaseMessage(buffer);

            log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

            if (MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()) {
                RequestHandler instance = RequestHandler.getInstance();
                instance.handle(baseMessage.toRequest(), (SocketChannel) ctx.channel());
            }
            if (MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()) {
                ResponseHandler instance = ResponseHandler.getInstance();
                instance.handle(baseMessage.toResponse());
            }
        }catch (Exception ex){
            log.error("read message occur error", ex);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        String gatewayChannelId = CommonUtil.getGatewayChannelId((SocketChannel) ctx.channel());
        log.error("client occur error: {}", gatewayChannelId, cause);
    }

}
