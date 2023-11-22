package com.hiraeth.im.dispatcher;

import com.hiraeth.im.common.Constant;
import com.hiraeth.im.dispatcher.handler.DispatcherHandler;
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
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/20 22:02
 */
@Slf4j
@SpringBootApplication
//@MapperScan("com.hiraeth.im.dispatcher")
public class DispatcherServer {

    private static final int PORT = 8090;
    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication();
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = SpringApplication.run(DispatcherServer.class);
        DispatcherHandler dispatcherHandler = context.getBeanFactory().getBean(DispatcherHandler.class);

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
                            socketChannel.pipeline().addLast(dispatcherHandler);
                        }
                    });
            ChannelFuture channelFuture = server.bind(PORT).sync();
            log.info("dispatcher server started. ");
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex){
            log.error("start dispatcher server occur error", ex);
        }finally {
            eventLoopGroup.shutdownGracefully();
            ioEventLoopGroup.shutdownGracefully();
        }
    }
}
