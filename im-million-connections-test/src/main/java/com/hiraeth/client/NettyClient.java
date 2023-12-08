package com.hiraeth.client;

import com.hiraeth.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lynch
 * @description:
 * @date: 2023/12/7 10:07
 */
public class NettyClient {

    public static void main(String[] args) {
        new NettyClient().run(NettyConfig.BEGIN_PORT, NettyConfig.END_PORT);
    }

    public void run(int beginPort, int endPort) {
        System.out.println("client starting ...");

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true) //快速复用端口
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                    }
                });

        int index = 0;

        AtomicInteger connections = new AtomicInteger();
        while (true) {
            int finalPort = beginPort + index;
            try {
//                Thread.sleep(100);
                bootstrap.connect(NettyConfig.REMOTE_SERVER_ADDR, finalPort).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        System.out.println("connect to server failed, port  " + finalPort +", total connections: "+ connections);
                    }else{
                        connections.getAndIncrement();
                        System.out.println("connect to server success, port  " + finalPort+", total connections: "+ connections);
                    }

                }).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            ++index;
            if (index == (endPort - beginPort)) {
                index = 0;
            }
        }

    }
}
