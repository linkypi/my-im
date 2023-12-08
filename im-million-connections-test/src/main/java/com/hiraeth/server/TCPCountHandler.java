package com.hiraeth.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: lynch
 * @description:
 * @date: 2023/12/7 10:02
 */
@ChannelHandler.Sharable
public class TCPCountHandler extends ChannelInboundHandlerAdapter {

    //使用原子类，避免线程安全问题
    private static final LongAdder longAdder = new LongAdder();

    public TCPCountHandler() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println(new Date().toString() + " the current number of connections is " + longAdder.longValue());
        }, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        longAdder.increment();
        NioSocketChannel clientChannel = (NioSocketChannel)ctx.channel();
        String clientAddr = clientChannel.remoteAddress().toString();
        String serverAddr = clientChannel.localAddress().toString();
        System.out.println(new Date() + " client connected "+ longAdder.longValue()
        + clientAddr + " -> "+ serverAddr);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date().toString() + " client disconnected.");
        longAdder.decrement();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(new Date().toString() + " TCPCountHandler exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}