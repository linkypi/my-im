package com.lynch.im.gateway.tcp;

import com.lynch.im.protocol.AuthenticateRequestProto.*;
import com.lynch.im.protocol.AuthenticateResponseProto.*;
import com.lynch.im.protocol.RequestTypeProto.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        ClientManager instance = ClientManager.getInstance();
        instance.removeClient(channel);
        log.info("client disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buffer = (ByteBuf) msg;
        Request request = new Request(buffer);
        log.info("receive msg, len: {}", request.getBodyLength());

        RequestHandler requestHandler = RequestHandler.getInstance();

        if( RequestType.AUTHENTICATE_VALUE == request.getRequestType()){
            AuthenticateRequest authenticateRequest = AuthenticateRequest.parseFrom(request.getBody());
            AuthenticateResponse authenticateResponse = requestHandler.authenticate(authenticateRequest);
            Response response = new Response(request, authenticateResponse.toByteArray());
            ctx.writeAndFlush(response.getBuffer());
            return;
        }



        String str =(String) msg;


        ClientManager instance = ClientManager.getInstance();
        String userId = str.split("\\|")[1];

        // 处理认证请求
        if (str.startsWith("auth")){

            String token = str.split("\\|")[2];

            instance.addClient(userId, (SocketChannel) ctx.channel());
            //检验 token 是否合法

            // 校验通过后缓存当前用户信息
            log.info("client {} authorize success", userId);
            return;
        }

        // 若相关用户连接不存在 则提示错误
        if(!instance.isClientConnected(userId)){
            log.info("user id: {} unauthorized", userId);
            byte[] res = "unauthorized".getBytes();
            ByteBuf buffer = Unpooled.buffer(res.length);
            buffer.writeBytes(res);
            ctx.writeAndFlush(buffer);
            return;
        }

        // 已认证
        log.info("user id: {} authorized" , userId);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught ", cause);
    }
}
