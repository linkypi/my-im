package com.hiraeth.im.sdk;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.Message;
import com.hiraeth.im.common.Request;
import com.hiraeth.im.protocol.AuthenticateRequestProto.*;
import com.hiraeth.im.protocol.MessageProto;
import com.hiraeth.im.protocol.RequestTypeProto.RequestType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

import static com.hiraeth.im.common.Constant.DELIMITER;

/**
 * @author leo
 * @ClassName IMClient
 * @description: TODO
 * @date 11/20/23 3:34 PM
 */
@Slf4j
public class IMClient {


    private SocketChannel socketChannel;
    private Bootstrap client;
    private EventLoopGroup threadGroup;
    private CountDownLatch latch = new CountDownLatch(1);

    public void connect(String host, int port){
        try {
            this.client = new Bootstrap();
            this.threadGroup = new NioEventLoopGroup();
            client.group(threadGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER);
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                            socketChannel.pipeline().addLast(new IMClientHandler(IMClient.this));
                        }
                    });
            log.info("The IM client configuration is complete");
            ChannelFuture channelFuture = client.connect(host, port);

            log.info("start connect to the gateway server.");
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        log.info("IM connection complete: {}", channelFuture.channel().remoteAddress());
                        socketChannel = (SocketChannel) channelFuture.channel();
                        latch.countDown();
                    }else{
                        log.error("IM connection occur error: {}", channelFuture.channel().remoteAddress());
                        channelFuture.channel().closeFuture();
                        threadGroup.shutdownGracefully();
                    }
                }
            });
            channelFuture.sync();
            latch.await();
        }catch (Exception ex){
            log.error("start im client occur error", ex);
        }
    }

    public void reconnect(){
        // 重新调用注册注册中心地址获取其他接入系统实例地址进行重连
        String host = "";
        int port = 8090;
        connect(host, port);
    }

    private boolean isConnected(){
        return socketChannel != null && socketChannel.isOpen();
    }

    public void authenticate(String userId, String token) {
        log.info("start authenticate...");

        AuthenticateRequest.Builder builder = AuthenticateRequest.newBuilder();
        builder.setTimestamp(System.currentTimeMillis());
        builder.setToken(token);
        builder.setUid(userId);
        AuthenticateRequest authenticateRequest = builder.build();
        Request request = new Request(Constant.APP_SDK_VERSION,
                RequestType.AUTHENTICATE, authenticateRequest.toByteArray());
        sendInternal(request);
    }

    public void send(String userId, String msg){
        log.info("send one msg to gateway server.");
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setTimestamp(System.currentTimeMillis());
        builder.setFromUid(userId);
        builder.setText(msg);
        Request request = new Request(Constant.APP_SDK_VERSION,
                RequestType.SEND_MESSAGE, builder.build().toByteArray());
        sendInternal(request);
//        sendInternal(msg + "|" + userId);
    }

    private void sendInternal(Request request) {
        if(!isConnected()){
            log.error("Cannot authorize, because connection incomplete or is closed.");
            return;
        }
        socketChannel.writeAndFlush(request.getBuffer());
    }

    public void close(){
       this.socketChannel.close();
       threadGroup.shutdownGracefully();
    }

}
