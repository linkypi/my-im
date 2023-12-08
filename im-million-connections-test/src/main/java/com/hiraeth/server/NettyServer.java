package com.hiraeth.server;

import com.hiraeth.NettyConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author: lynch
 * @description:
 * @date: 2023/12/7 10:01
 */
public class NettyServer {
    public void run(int beginPort, int endPort) {
        System.out.println("server starting ...");
        //配置服务端线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
//                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true); //快速复用端口

        serverBootstrap.childHandler(new TCPCountHandler());

        // 为模拟百万连接, 服务端可以监听20个端口, 而客户端正常有6万多端口可用, 如此连接即可轻松达到百万连接
        for (; beginPort < endPort; beginPort++) {
            int port = beginPort;
            serverBootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                System.out.println("binding port  " + port);
            });
        }

    }

    /**
     * 启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        new NettyServer().run(NettyConfig.BEGIN_PORT, NettyConfig.END_PORT);
    }
}
