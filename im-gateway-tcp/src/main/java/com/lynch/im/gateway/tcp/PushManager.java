package com.lynch.im.gateway.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author leo
 * @ClassName PushManager
 * @description: TODO
 * @date 11/20/23 4:16 PM
 */
@Slf4j
public class PushManager {

    public void start() {
        new PushThread().start();
    }

    static class PushThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30 * 1000);
                    String userId = "test001";

                    NettyChannelManager instance = NettyChannelManager.getInstance();
                    SocketChannel channel = instance.getChannel(userId);
                    if(channel!=null) {
                        byte[] res = "push one msg".getBytes();
                        ByteBuf buffer = Unpooled.buffer(res.length);
                        buffer.writeBytes(res);
                        channel.writeAndFlush(buffer);
                    }else{
                        log.info("user id [{}] be off line", userId);
                    }
                } catch (Exception ex) {
                    log.error("push manager occur error", ex);
                }
            }
        }
    }
}
