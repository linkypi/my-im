package com.hiraeth.im.gateway.tcp.dispatcher;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hiraeth.im.common.entity.BaseMessage;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.common.entity.Response;
import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.gateway.tcp.SessionManager;
import com.hiraeth.im.protocol.*;
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
 * @date: 2023/11/20 23:23
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DispatcherInstanceHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
        DispatcherInstanceManager.removeDispatcherInstance(gatewayChannelId);
        log.info("gateway disconnected: {}", gatewayChannelId);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String gatewayChannelId = CommonUtil.getGatewayChannelId(channel);
        log.info("gateway connected: {}", gatewayChannelId);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        BaseMessage baseMessage = new BaseMessage(buffer);

        try {
            log.info("receive msg, message type: {}, request type: {}, sequence: {}",
                    baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence());

            if (MessageTypeEnum.MessageType.REQUEST == baseMessage.getMessageType()) {
                Request request = baseMessage.toRequest();
                if(RequestTypeProto.RequestType.FORWARD_MESSAGE_VALUE == baseMessage.getRequestType()) {
                    forwardMsgToClient(request, (SocketChannel) ctx.channel());
                }
                return;
            }

            if (MessageTypeEnum.MessageType.RESPONSE == baseMessage.getMessageType()) {
                Response response = baseMessage.toResponse();
                log.info("receive msg from dispatcher server: {}", JSON.toJSONString(response));
                if (RequestTypeProto.RequestType.AUTHENTICATE_VALUE == response.getRequestType()) {
                    forwardAuthRequest(ctx, response);
                    return;
                }
                if (RequestTypeProto.RequestType.SEND_MESSAGE_VALUE == response.getRequestType()) {
                    forwardSendMsgResponse(response);
                    return;
                }
                // 将响应返回
//                ctx.writeAndFlush(response.getBuffer());
            }
        }catch (Exception ex){
            log.error("handle message occur error, message type: {}, request type: {}, sequence: {}",
                   baseMessage.getMessageType(), baseMessage.getRequestType(), baseMessage.getSequence(), ex);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void forwardMsgToClient(Request request, SocketChannel channel) throws InvalidProtocolBufferException {

        MessageForwardRequestProto.MessageForwardRequest forwardRequest = MessageForwardRequestProto.MessageForwardRequest.parseFrom(request.getBody());
        log.info("forward msg to receiver: {}", JSON.toJSONString(forwardRequest));

        DispatcherInstanceManager.addDispatcherChannel(forwardRequest.getReceiverId(), channel);
        // 将响应转发到此前请求过来的 App 会话
        SocketChannel session = SessionManager.getSession(forwardRequest.getReceiverId());
        if (session == null) {
            log.warn("forward message to receiver failed, because session not found, receiver id: {}, msg: {}",
                    forwardRequest.getReceiverId(), JSON.toJSONString(forwardRequest));
            return;
        }
        session.writeAndFlush(request.getBuffer());
    }

    /**
     * 回复消息发送者相关响应
     * @param response
     * @throws InvalidProtocolBufferException
     */
    private void forwardSendMsgResponse(Response response) throws InvalidProtocolBufferException {
        MessageSendResponseProto.MessageSendResponse msg = MessageSendResponseProto.MessageSendResponse.parseFrom(response.getBody());
        log.info("forward sender message response to app client: {}", JSON.toJSONString(msg));

        // 将响应转发到此前请求过来的 App 会话
        SocketChannel session = SessionManager.getSession(msg.getSenderId());
        if (session == null) {
            log.warn("forward sender message response failed, because session not found, sender id: {}, msg: {}",
                    msg.getSenderId(), JSON.toJSONString(msg));
            return;
        }
        session.writeAndFlush(response.getBuffer());
    }

    private void forwardAuthRequest(ChannelHandlerContext ctx, Response response) throws InvalidProtocolBufferException {
        AuthenticateResponseProto.AuthenticateResponse authenticateResponse = AuthenticateResponseProto.AuthenticateResponse.parseFrom(response.getBody());

        // 将响应转发到此前请求过来的 App 会话
        SocketChannel session = SessionManager.getSession(authenticateResponse.getUid());
        session.writeAndFlush(response.getBuffer());

        // 若认证通过则需设置本地session以及Redis中的分布式session
        if (authenticateResponse.getSuccess()) {
            log.info("authenticate success, userId: {}", authenticateResponse.getUid());
//            SocketChannel channel = (SocketChannel) ctx.channel();
            return;
        }
        log.info("authenticate failed, response: {}", JSON.toJSONString(authenticateResponse));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        String gatewayChannelId = CommonUtil.getGatewayChannelId((SocketChannel) ctx.channel());
        log.error("client occur error: {}", gatewayChannelId, cause);
    }

}
