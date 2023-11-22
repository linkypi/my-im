package com.hiraeth.im.gateway.tcp;

import com.hiraeth.im.gateway.tcp.dispatcher.DispatcherInstanceManager;
import com.hiraeth.im.gateway.tcp.push.PushManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static com.hiraeth.im.common.Constant.DELIMITER;

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
        SpringApplication springApplication = new SpringApplication();
        springApplication.setWebApplicationType(WebApplicationType.NONE);
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
                            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER);
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(4096, delimiter));
                            socketChannel.pipeline().addLast(gatewayTcpHandler);
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
}
