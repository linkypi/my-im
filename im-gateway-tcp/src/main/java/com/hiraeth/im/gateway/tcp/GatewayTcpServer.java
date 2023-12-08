package com.hiraeth.im.gateway.tcp;

import com.hiraeth.im.gateway.tcp.push.PushManager;
import com.hiraeth.im.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author leo
 * @ClassName GatewayTcpServer
 * @description: TODO
 * @date 11/20/23 3:04 PM
 */
@Slf4j
@SpringBootApplication
public class GatewayTcpServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
        System.setProperty("rocketmq.client.logLevel", "ERROR");

        ConfigurableApplicationContext context = SpringApplication.run(GatewayTcpServer.class);
        GatewayTcpHandler gatewayTcpHandler = context.getBeanFactory().getBean(GatewayTcpHandler.class);

        // 启动消息推送组件
        new PushManager().start();

//         启动分发系统实例管理组件, 已通过 spring 启动流程完成初始化
//        DispatcherInstanceManager dispatcherInstanceManager = new DispatcherInstanceManager();
//        dispatcherInstanceManager.init();

        listenServer(gatewayTcpHandler);
    }

    private static void listenServer(GatewayTcpHandler gatewayTcpHandler) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup ioEventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(eventLoopGroup, ioEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER);
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                            socketChannel.pipeline().addLast(gatewayTcpHandler);
                            socketChannel.pipeline().addLast(new IdleStateHandler(30,0,30, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast("", new IdleHandler());
                        }
                    });
            ChannelFuture channelFuture = server.bind(PORT).sync();
            log.info("gateway server started. ");
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            log.error("start gateway server occur error", ex);
        }finally {
            eventLoopGroup.shutdownGracefully();
            ioEventLoopGroup.shutdownGracefully();
        }
    }

    static class IdleHandler extends ChannelDuplexHandler {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                if(idleStateEvent.state() == IdleState.READER_IDLE){
                    // 长时间没有收到客户端消息, 可以断开连接
                    ctx.channel().close();
                }
            }

        }
    }
}
