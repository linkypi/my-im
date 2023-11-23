package com.hiraeth.im.sdk;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.common.entity.Request;
import com.hiraeth.im.common.util.CommonUtil;
import com.hiraeth.im.protocol.AuthenticateRequestProto.*;
import com.hiraeth.im.protocol.ChatTypeEnum;
import com.hiraeth.im.protocol.MediaTypeEnum;
import com.hiraeth.im.protocol.MessageSendRequestProto;
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
import java.util.function.Consumer;

/**
 * @author leo
 * @ClassName IMClient
 * @description: TODO
 * @date 11/20/23 3:34 PM
 */
@Slf4j
public class IMClient {

    private SocketChannel socketChannel;
    private EventLoopGroup threadGroup;
    private CountDownLatch latch = null;

    private void connect(String host, int port, Consumer<Boolean> callback) {
        try {
            Bootstrap client = new Bootstrap();
            this.threadGroup = new NioEventLoopGroup();
            client.group(threadGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER);
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                            socketChannel.pipeline().addLast(new IMClientHandler(IMClient.this));
                        }
                    });
            log.info("The IM client configuration is complete");
            ChannelFuture channelFuture = client.connect(host, port);

            log.info("start connect to the gateway server.");
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> handle(channelFuture1, callback));
            channelFuture.sync();
        } catch (Exception ex) {
            log.error("start im client occur error", ex);
        }
    }

    private void handle(ChannelFuture channelFuture1, Consumer<Boolean> callback) {
        boolean success = false;
        String gatewayChannelId = CommonUtil.getGatewayChannelId((SocketChannel) channelFuture1.channel());
        if (channelFuture1.isSuccess()) {
            success = true;
            log.info("IM connection complete: {}", gatewayChannelId);
            socketChannel = (SocketChannel) channelFuture1.channel();
        } else {
            log.error("IM connection occur error: {}", gatewayChannelId);
            channelFuture1.channel().closeFuture();
            threadGroup.shutdownGracefully();
        }
        if (callback != null) {
            callback.accept(success);
        }
        if (latch != null) {
            latch.countDown();
        }
    }

    /**
     * 发起同步连接
     * @param host
     * @param port
     */
    public void connectSync(String host, int port) {
        try {
            latch = new CountDownLatch(1);
            connect(host, port, null);
            latch.await();
        } catch (Exception ex) {
            log.error("start im client occur error", ex);
        }
    }

    /**
     * 发起异步连接
     *
     * @param host
     * @param port
     * @param callback 连接成功返回true， 连接失败返回false
     */
    public void connectAsync(String host, int port, Consumer<Boolean> callback) {
        try {
            connect(host, port, callback);
        } catch (Exception ex) {
            log.error("start im client occur error", ex);
        }
    }

    public void reconnect() {
        // 重新调用注册注册中心地址获取其他接入系统实例地址进行重连
        String host = "";
        int port = 8090;
        connectSync(host, port);

        // 重新发起认证
//        authenticate();
    }

    private boolean isConnected() {
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

    public void sendMessage(String fromUid, String toUid, String msg) {
        log.info("send one msg to gateway server.");
        MessageSendRequestProto.MessageSendRequest.Builder builder = MessageSendRequestProto.MessageSendRequest.newBuilder();
        builder.setTimestamp(System.currentTimeMillis());
        builder.setChatType(ChatTypeEnum.ChatType.SINGLE);
        builder.setMediaType(MediaTypeEnum.MediaType.TEXT);
        builder.setFromUid(fromUid);
        builder.setToUid(toUid);
        builder.setContent(msg);
        Request request = new Request(Constant.APP_SDK_VERSION,
                RequestType.SEND_MESSAGE, builder.build().toByteArray());
        sendInternal(request);
    }

    private void sendInternal(Request request) {
        if (!isConnected()) {
            log.error("Cannot authorize, because connection incomplete or is closed.");
            return;
        }
        socketChannel.writeAndFlush(request.getBuffer());
    }

    public void close() {
        this.socketChannel.close();
        threadGroup.shutdownGracefully();
    }

}
